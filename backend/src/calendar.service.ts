import { google } from 'googleapis';
import { OAuth2Client } from 'google-auth-library';
import type { CalendarEvent, CreateMilestoneRequest, CreateTaskRequest } from './calendar.types';
import logger from './logger.util';

export class CalendarService {
  private oauth2Client: OAuth2Client;

  constructor() {
    this.oauth2Client = new OAuth2Client(
      process.env.GOOGLE_CLIENT_ID,
      process.env.GOOGLE_CLIENT_SECRET,
      process.env.GOOGLE_REDIRECT_URI || 'http://localhost:3001/api/auth/google/callback'
    );
  }

  /**
   * Set credentials for the OAuth2 client
   */
  setCredentials(accessToken: string, refreshToken?: string) {
    this.oauth2Client.setCredentials({
      access_token: accessToken,
      refresh_token: refreshToken,
    });
  }

  /**
   * Get upcoming CPEN 321 milestones from user's calendar
   */
  async getUpcomingMilestones(accessToken: string, maxResults: number = 10): Promise<CalendarEvent[]> {
    try {
      this.setCredentials(accessToken);
      
      const calendar = google.calendar({ version: 'v3', auth: this.oauth2Client });
      const now = new Date();
      const oneMonthFromNow = new Date();
      oneMonthFromNow.setMonth(oneMonthFromNow.getMonth() + 1);

      // Get all events in the next month
      const response = await calendar.events.list({
        calendarId: 'primary',
        timeMin: now.toISOString(),
        timeMax: oneMonthFromNow.toISOString(),
        maxResults: 50, // Get more events to filter
        singleEvents: true,
        orderBy: 'startTime',
      });

      const events = response.data.items || [];
      
      // Filter for CPEN 321 related events
      const cpen321Events = events.filter(event => {
        if (!event.summary) return false;
        
        const summary = event.summary.toLowerCase();
        const description = (event.description || '').toLowerCase();
        
        // Check if event contains CPEN 321 keywords
        return this.isCPEN321Event(summary, description);
      });

      // Sort by start time and limit results
      return cpen321Events
        .slice(0, maxResults)
        .map(event => this.mapGoogleEventToCalendarEvent(event));
        
    } catch (error) {
      logger.error('Error fetching upcoming milestones:', error);
      throw new Error('Failed to fetch upcoming milestones');
    }
  }

  /**
   * Get today's schedule from user's calendar
   */
  async getTodaysSchedule(accessToken: string): Promise<CalendarEvent[]> {
    try {
      this.setCredentials(accessToken);
      
      const calendar = google.calendar({ version: 'v3', auth: this.oauth2Client });
      const now = new Date();
      const startOfDay = new Date(now.getFullYear(), now.getMonth(), now.getDate());
      const endOfDay = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1);

      const response = await calendar.events.list({
        calendarId: 'primary',
        timeMin: startOfDay.toISOString(),
        timeMax: endOfDay.toISOString(),
        maxResults: 20,
        singleEvents: true,
        orderBy: 'startTime',
      });

      const events = response.data.items || [];
      
      return events.map(event => this.mapGoogleEventToCalendarEvent(event));
        
    } catch (error) {
      logger.error('Error fetching today\'s schedule:', error);
      throw new Error('Failed to fetch today\'s schedule');
    }
  }

  /**
   * Check if an event is CPEN 321 related
   */
  private isCPEN321Event(summary: string, description: string): boolean {
    const cpen321Keywords = [
      'cpen 321',
      'cpen321',
      'assignment',
      'project',
      'milestone',
      'deadline',
      'due',
      'exam',
      'quiz',
      'lab',
      'homework',
      'deliverable'
    ];

    const text = `${summary} ${description}`;
    
    return cpen321Keywords.some(keyword => 
      text.includes(keyword.toLowerCase())
    );
  }

  /**
   * Map Google Calendar event to our CalendarEvent interface
   */
  private mapGoogleEventToCalendarEvent(event: any): CalendarEvent {
    return {
      id: event.id || '',
      summary: event.summary || 'No Title',
      description: event.description || '',
      start: {
        dateTime: event.start?.dateTime,
        date: event.start?.date,
      },
      end: {
        dateTime: event.end?.dateTime,
        date: event.end?.date,
      },
      location: event.location || '',
      htmlLink: event.htmlLink || '',
      created: event.created || '',
      updated: event.updated || '',
    };
  }

  /**
   * Create a new milestone event
   */
  async createMilestone(accessToken: string, milestoneData: CreateMilestoneRequest): Promise<CalendarEvent> {
    try {
      this.setCredentials(accessToken);
      
      const calendar = google.calendar({ version: 'v3', auth: this.oauth2Client });
      
      // Format the event data
      const eventData = this.formatEventData(milestoneData, 'milestone');
      
      const response = await calendar.events.insert({
        calendarId: 'primary',
        requestBody: eventData,
      });

      const event = response.data;
      if (!event) {
        throw new Error('Failed to create milestone event');
      }

      return this.mapGoogleEventToCalendarEvent(event);
    } catch (error) {
      logger.error('Error creating milestone:', error);
      throw new Error('Failed to create milestone');
    }
  }

  /**
   * Create a new task event
   */
  async createTask(accessToken: string, taskData: CreateTaskRequest): Promise<CalendarEvent> {
    try {
      this.setCredentials(accessToken);
      
      const calendar = google.calendar({ version: 'v3', auth: this.oauth2Client });
      
      // Format the event data
      const eventData = this.formatEventData(taskData, 'task');
      
      const response = await calendar.events.insert({
        calendarId: 'primary',
        requestBody: eventData,
      });

      const event = response.data;
      if (!event) {
        throw new Error('Failed to create task event');
      }

      return this.mapGoogleEventToCalendarEvent(event);
    } catch (error) {
      logger.error('Error creating task:', error);
      throw new Error('Failed to create task');
    }
  }

  /**
   * Delete an event by ID
   */
  async deleteEvent(accessToken: string, eventId: string): Promise<void> {
    try {
      this.setCredentials(accessToken);
      
      const calendar = google.calendar({ version: 'v3', auth: this.oauth2Client });
      
      await calendar.events.delete({
        calendarId: 'primary',
        eventId: eventId,
      });
    } catch (error) {
      logger.error('Error deleting event:', error);
      throw new Error('Failed to delete event');
    }
  }

  /**
   * Format event data for Google Calendar API
   */
  private formatEventData(data: CreateMilestoneRequest | CreateTaskRequest, type: 'milestone' | 'task'): any {
    const { title, description, dueDate, dueTime, isAllDay } = data;
    
    // Create summary with appropriate prefix
    const summary = type === 'milestone' 
      ? `CPEN 321 ${title}` 
      : `Task: ${title}`;
    
    // Format start and end times
    let start: any, end: any;
    
    if (isAllDay) {
      start = { date: dueDate };
      end = { date: dueDate };
    } else if (dueTime) {
      const startDateTime = `${dueDate}T${dueTime}:00`;
      const endDateTime = `${dueDate}T${dueTime}:00`;
      start = { dateTime: startDateTime };
      end = { dateTime: endDateTime };
    } else {
      // Default to all day if no time specified
      start = { date: dueDate };
      end = { date: dueDate };
    }
    
    return {
      summary,
      description: description || `Created via CPEN 321 App - ${type}`,
      start,
      end,
      reminders: {
        useDefault: false,
        overrides: [
          { method: 'popup', minutes: 60 }, // 1 hour before
          { method: 'popup', minutes: 1440 }, // 1 day before
        ],
      },
    };
  }

  /**
   * Get authorization URL for Calendar API access
   */
  getAuthUrl(): string {
    const scopes = [
      'https://www.googleapis.com/auth/calendar',
      'https://www.googleapis.com/auth/userinfo.email',
      'https://www.googleapis.com/auth/userinfo.profile'
    ];

    return this.oauth2Client.generateAuthUrl({
      access_type: 'offline',
      scope: scopes,
      prompt: 'consent'
    });
  }
}

export const calendarService = new CalendarService();
