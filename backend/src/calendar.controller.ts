import { NextFunction, Request, Response } from 'express';
import { calendarService } from './calendar.service';
import { 
  GetUpcomingMilestonesResponse, 
  GetTodaysScheduleResponse,
  CreateMilestoneRequest,
  CreateTaskRequest,
  CreateEventResponse,
  DeleteEventResponse
} from './calendar.types';
import logger from './logger.util';

export class CalendarController {
  /**
   * Get upcoming CPEN 321 milestones
   */
  async getUpcomingMilestones(
    req: Request,
    res: Response<GetUpcomingMilestonesResponse>,
    next: NextFunction
  ) {
    try {
      const user = req.user!;
      
      // For now, we'll use the user's Google access token
      // In a real implementation, you'd store the access token during OAuth flow
      const accessToken = req.headers.authorization?.replace('Bearer ', '');
      
      if (!accessToken) {
        return res.status(401).json({
          message: 'Access token required for calendar access',
        });
      }

      const milestones = await calendarService.getUpcomingMilestones(accessToken);

      return res.status(200).json({
        message: 'Upcoming milestones retrieved successfully',
        data: {
          milestones,
        },
      });
    } catch (error) {
      logger.error('Error getting upcoming milestones:', error);

      if (error instanceof Error) {
        return res.status(500).json({
          message: error.message || 'Failed to retrieve upcoming milestones',
        });
      }

      next(error);
    }
  }

  /**
   * Get today's schedule
   */
  async getTodaysSchedule(
    req: Request,
    res: Response<GetTodaysScheduleResponse>,
    next: NextFunction
  ) {
    try {
      const user = req.user!;
      
      // For now, we'll use the user's Google access token
      const accessToken = req.headers.authorization?.replace('Bearer ', '');
      
      if (!accessToken) {
        return res.status(401).json({
          message: 'Access token required for calendar access',
        });
      }

      const events = await calendarService.getTodaysSchedule(accessToken);

      return res.status(200).json({
        message: 'Today\'s schedule retrieved successfully',
        data: {
          events,
        },
      });
    } catch (error) {
      logger.error('Error getting today\'s schedule:', error);

      if (error instanceof Error) {
        return res.status(500).json({
          message: error.message || 'Failed to retrieve today\'s schedule',
        });
      }

      next(error);
    }
  }

  /**
   * Create a new milestone
   */
  async createMilestone(
    req: Request<unknown, unknown, CreateMilestoneRequest>,
    res: Response<CreateEventResponse>,
    next: NextFunction
  ) {
    try {
      const user = req.user!;
      const accessToken = req.headers.authorization?.replace('Bearer ', '');
      
      if (!accessToken) {
        return res.status(401).json({
          message: 'Access token required for calendar access',
        });
      }

      const milestone = await calendarService.createMilestone(accessToken, req.body);

      return res.status(201).json({
        message: 'Milestone created successfully',
        data: {
          event: milestone,
        },
      });
    } catch (error) {
      logger.error('Error creating milestone:', error);

      if (error instanceof Error) {
        return res.status(500).json({
          message: error.message || 'Failed to create milestone',
        });
      }

      next(error);
    }
  }

  /**
   * Create a new task
   */
  async createTask(
    req: Request<unknown, unknown, CreateTaskRequest>,
    res: Response<CreateEventResponse>,
    next: NextFunction
  ) {
    try {
      const user = req.user!;
      const accessToken = req.headers.authorization?.replace('Bearer ', '');
      
      if (!accessToken) {
        return res.status(401).json({
          message: 'Access token required for calendar access',
        });
      }

      const task = await calendarService.createTask(accessToken, req.body);

      return res.status(201).json({
        message: 'Task created successfully',
        data: {
          event: task,
        },
      });
    } catch (error) {
      logger.error('Error creating task:', error);

      if (error instanceof Error) {
        return res.status(500).json({
          message: error.message || 'Failed to create task',
        });
      }

      next(error);
    }
  }

  /**
   * Delete an event
   */
  async deleteEvent(
    req: Request<{ eventId: string }>,
    res: Response<DeleteEventResponse>,
    next: NextFunction
  ) {
    try {
      const user = req.user!;
      const { eventId } = req.params;
      const accessToken = req.headers.authorization?.replace('Bearer ', '');
      
      if (!accessToken) {
        return res.status(401).json({
          message: 'Access token required for calendar access',
        });
      }

      await calendarService.deleteEvent(accessToken, eventId);

      return res.status(200).json({
        message: 'Event deleted successfully',
      });
    } catch (error) {
      logger.error('Error deleting event:', error);

      if (error instanceof Error) {
        return res.status(500).json({
          message: error.message || 'Failed to delete event',
        });
      }

      next(error);
    }
  }

  /**
   * Get calendar authorization URL
   */
  async getAuthUrl(req: Request, res: Response, next: NextFunction) {
    try {
      const authUrl = calendarService.getAuthUrl();

      return res.status(200).json({
        message: 'Authorization URL generated successfully',
        data: {
          authUrl,
        },
      });
    } catch (error) {
      logger.error('Error generating auth URL:', error);

      if (error instanceof Error) {
        return res.status(500).json({
          message: error.message || 'Failed to generate authorization URL',
        });
      }

      next(error);
    }
  }
}
