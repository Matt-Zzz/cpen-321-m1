// MongoDB initialization script
// This script runs when the MongoDB container starts for the first time

// Switch to the application database
db = db.getSiblingDB('usermanagement');

// Create a user for the application
db.createUser({
  user: 'app_user',
  pwd: 'app_password',
  roles: [
    {
      role: 'readWrite',
      db: 'usermanagement'
    }
  ]
});

// Create collections with validation
db.createCollection('users', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['googleId', 'email', 'name'],
      properties: {
        googleId: {
          bsonType: 'string',
          description: 'Google ID must be a string and is required'
        },
        email: {
          bsonType: 'string',
          pattern: '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$',
          description: 'Email must be a valid email address and is required'
        },
        name: {
          bsonType: 'string',
          minLength: 1,
          description: 'Name must be a string with at least 1 character and is required'
        },
        profilePicture: {
          bsonType: 'string',
          description: 'Profile picture must be a string'
        },
        bio: {
          bsonType: 'string',
          maxLength: 500,
          description: 'Bio must be a string with maximum 500 characters'
        },
        hobbies: {
          bsonType: 'array',
          items: {
            bsonType: 'string'
          },
          description: 'Hobbies must be an array of strings'
        }
      }
    }
  }
});

// Create indexes for better performance
db.users.createIndex({ googleId: 1 }, { unique: true });
db.users.createIndex({ email: 1 }, { unique: true });
db.users.createIndex({ createdAt: 1 });

print('Database initialization completed successfully!');
