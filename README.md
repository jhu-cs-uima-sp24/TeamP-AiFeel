# TeamP-AiFeel
AiFeel is a comforting app which allows users to write journal entries. Our unique approach of integrating AI within journaling allows users to get a better sense of their moods while journaling, keep track of mood stats/analytics through visualization techniques, and talk to a customizable AI Pen-pal to help interpret journal entries & provide assistance/advice whenever needed.

You are able to login through any email and password by creating your own account and signing into it.
As a functioning example, you can login with the following info with prepopulated data
- Email: demo_t5_v2@jhu.edu 
- Password: 12345678

Note: Please use either the above demo account OR make a new account. Do not use other existing/old accounts as they might not be fully integrated with our updated database structure.

# T6 Final Wrapup
We dubbugged our program based on feedback. 1) the edit profile/pal page does work with special characters, but you would need a new account from sprint 1, as we made database changes. Please try the provided account as an example or create a new one. 2) Also fixed bugs on the main page and stats. 3) fixed journal, main views for smaller and wider screens

# Sprint 2:
## General State
The app is at its finished state. Most of this sprint was aimed at integrating the database as well as AI features in the app.

## Implemented Features

### Login Activity
- Change passward reset mechanism to the login activity
- Change ImageButton to TextButton for sign up for better UI
- Error prevention for too simple password
- Implemented profile picture upload feature with firebase
  
### Home Fragment
- Streak implementation
    - Streak counter updating text at top
    - Updating streak fire visualizer corresponding to day of week which user has journaled in the current week
- Calendar
    - Calendar future date cell error handling
    - Calendar future month arrow selector error handling
    - Calendar dialog future year arrow selector error handling
    - Calendar dialog future month button error handling
    - Mood emoji display in calendar cell background
- Data visualization
    - Line chart for mood changes
    - Line chart added gradient backgound
    - Line chart with customized icon as y-axis
    - Piechart for monthly mood distribution
      
### Journal Fragment
 - Implementation of old journal entries
        - Navigatable from the calendar view
        - Error handling for sending an old entry to the AI
 - Firebase integration of each entry for each date
 - GPT integration to generate AI journal responses (Lisa also helped with this)
 - Some error handling to minimize AI hallucinations through prompting

### Chat Fragment
- API call to allow user to chat with our chatbot
- Cloud storage of chat history
- Personalized chatbot role that fits the chatbot's age, gender, and persona
- Different behavior when talking to users with different age and gender
- Clear chat history button in the action bar 

### Profile Fragment 
- Additional Error handling with storing user information in the database
- More detailed persona builder using user selected personalities
- Implemented profile picture upload feature, which is also displayed in chat


# Sprint 1:
## General State
We have implemented most of the basic features that users interact with--login/signup, starting a journal entry, customizing their profile, and calendar view. For next sprint we'll work on AI and more advanced database storage. 

## Implemented Features
### Login/Signup
- login in with email and password using firebase authentication
- Sent error message if email and password do not match/email not registered, email/password textfield is empty
- Navigation to sign up/forgot password page
- User can enter their email to send a reset password link if they have previously created an account
- User can enter their email to create an account 
-  if email format is incorrect, will display error message
- User can sign up an account, with name, email, password
- Implemented email password authentication—> the new email and password entry will be added to firebase authentication
- Connected app to firebase realtime database —> user data is store on firebase
- Error prevention:
    - Display error message if password and retyped password do not match
    - Display error message if text fields are empty
    - Display error message if email format is incorrect
- (Extra implementation not planned for Spring 1) User can customize profile picture with camera photos or photo album), this is only implemented in CreateProfile Activity and is not connected with firebase database yet.

### Journal Entry
 - Today's journal entry pre-populated with today's date
 - Allows for user input of the journal entry
 - Constraints on the user input to fit the journal page
 - Clicking the send button indicates a new message in the mailbox from the AI
 - Clicking the mailbox button clears the inbox and opens a pop-up with the AI response
 - Journal entry is saved automatically throughout the course of the day
 - Mailbox status (new mail vs no new mail) is saved throughout the course of the day
 - Navigatable from the NavBar

### Calendar
- Created calendar xml file to display calendar on home page
- Functioning calendar so that it is linked so that date layout corresponds to months/years
- Calendar dates with onclick listeners that can be incorporated to perform actions
- Left and right arrow buttons on top of calendar to move between months
- Calendar icon button on top right to open dialog, allowing users to quickly select month and year
- Left and right arrow buttons in date selector dialog to move between years
- Buttons in dialog which can be highlighted to select months
- Streak display in xml file, with slots for empty and non-empty circles to keep track of streak
- Inclusion of mood emoji in drawables which can be synced to calendar cells
  
### Profile
- User email, name, age, gender, notification setting, and pal data are prepopulated from firebase data
- Edit profile button leads to edit profile activity
- Edit pal button leads to edit pal activity
- Text input for name
- Number input for age
- Spinner for gender and notification
- Search bar for personas through a list of about 30 personalities, filtering based on matching query and personality name in the adapter
- Clicking on an item in the search bar will add it to the persona list
- Remove button on the personas to remove from firebase
- NOTE: only a maximum of 3 personalities can be selected, to optimize AI performance
- Save buttons save the data to firebase and goes back to profile page
- Back button goes back without saving data

## Work in Progress
### Login/Signup
- store user profile image to firebase
### Journal Entry
 - Firebase connection to store all journal entries from past dates
 - Mavigation from the calendar view to see the current days' entry and past entries
 - Disabling input for past day's entries
 - Clearing the current journal entnry after the day has passed
 - Ensuring the view fits on multiple screen sizes (Doesn't work on Pixel 2, works on 3 and higher)
 - Connecting AI queries to user prompts and AI responses

### Calendar
As a result of moving journal entry storage in database to sprint 2 (as given by instructor), the following will happen for sprint 2 in regard to the home page:
1. Calendar boxes will directly bring user to journal entry. Calendar from sprint 1 is set up (since cells are already generated with onclicklisteners) to easily accomplish this and link to database.
2. Calendar boxes will have mood emojis in background of cell. This can easily be done once database of journal entries are done and when AI parses the emoji.
3. Streak will update based off how many journal entries are made in a row depending on database

### Profile
For sprint 2, we'll also implement profile image upload
