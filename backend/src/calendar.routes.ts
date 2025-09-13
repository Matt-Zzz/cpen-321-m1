import { Router } from 'express';
import { CalendarController } from './calendar.controller';
import { validateBody } from './validation.middleware';
import { createMilestoneSchema, createTaskSchema } from './calendar.types';

const router = Router();
const calendarController = new CalendarController();

// Get upcoming CPEN 321 milestones
router.get('/milestones', calendarController.getUpcomingMilestones);

// Get today's schedule
router.get('/schedule', calendarController.getTodaysSchedule);

// Create a new milestone
router.post('/milestones', validateBody(createMilestoneSchema), calendarController.createMilestone);

// Create a new task
router.post('/tasks', validateBody(createTaskSchema), calendarController.createTask);

// Delete an event
router.delete('/events/:eventId', calendarController.deleteEvent);

// Get calendar authorization URL
router.get('/auth-url', calendarController.getAuthUrl);

export default router;
