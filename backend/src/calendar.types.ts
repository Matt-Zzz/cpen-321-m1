import { z } from 'zod';

// Calendar Event Types
// ------------------------------------------------------------
export interface CalendarEvent {
  id: string;
  summary: string;
  description?: string;
  start: {
    dateTime?: string;
    date?: string;
  };
  end: {
    dateTime?: string;
    date?: string;
  };
  location?: string;
  htmlLink?: string;
  created: string;
  updated: string;
}

// Request/Response Types
// ------------------------------------------------------------
export interface GetUpcomingMilestonesResponse {
  message: string;
  data?: {
    milestones: CalendarEvent[];
  };
}

export interface GetTodaysScheduleResponse {
  message: string;
  data?: {
    events: CalendarEvent[];
  };
}

export interface CreateMilestoneRequest {
  title: string;
  description?: string;
  dueDate: string; // ISO date string
  dueTime?: string; // HH:MM format
  isAllDay?: boolean;
}

export interface CreateTaskRequest {
  title: string;
  description?: string;
  dueDate: string; // ISO date string
  dueTime?: string; // HH:MM format
  isAllDay?: boolean;
}

export interface CreateEventResponse {
  message: string;
  data?: {
    event: CalendarEvent;
  };
}

export interface DeleteEventResponse {
  message: string;
}

// Zod Schemas
// ------------------------------------------------------------
export const calendarEventSchema = z.object({
  id: z.string(),
  summary: z.string(),
  description: z.string().optional(),
  start: z.object({
    dateTime: z.string().optional(),
    date: z.string().optional(),
  }),
  end: z.object({
    dateTime: z.string().optional(),
    date: z.string().optional(),
  }),
  location: z.string().optional(),
  htmlLink: z.string().optional(),
  created: z.string(),
  updated: z.string(),
});

export const createMilestoneSchema = z.object({
  title: z.string().min(1, 'Title is required').max(100, 'Title too long'),
  description: z.string().max(500, 'Description too long').optional(),
  dueDate: z.string().regex(/^\d{4}-\d{2}-\d{2}$/, 'Invalid date format (YYYY-MM-DD)'),
  dueTime: z.string().regex(/^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/, 'Invalid time format (HH:MM)').optional(),
  isAllDay: z.boolean().optional(),
});

export const createTaskSchema = z.object({
  title: z.string().min(1, 'Title is required').max(100, 'Title too long'),
  description: z.string().max(500, 'Description too long').optional(),
  dueDate: z.string().regex(/^\d{4}-\d{2}-\d{2}$/, 'Invalid date format (YYYY-MM-DD)'),
  dueTime: z.string().regex(/^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/, 'Invalid time format (HH:MM)').optional(),
  isAllDay: z.boolean().optional(),
});

// Utility Types
// ------------------------------------------------------------
export type CalendarEventData = z.infer<typeof calendarEventSchema>;
export type CreateMilestoneRequestData = z.infer<typeof createMilestoneSchema>;
export type CreateTaskRequestData = z.infer<typeof createTaskSchema>;

// CPEN 321 specific keywords for milestone detection
export const CPEN321_KEYWORDS = [
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
