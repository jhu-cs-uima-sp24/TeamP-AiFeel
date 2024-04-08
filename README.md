# TeamP-AiFeel
You are able to login through any email and password by creating your own account and signing into it.
As a functioning example, you can login with the following info with prepopulated data
- Email: ericzhy0815@outlook.com
- Password: zhaohongyu0815

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
### Journal Entry
### Calendar
As a result of moving journal entry storage in database to sprint 2 (as given by instructor), the following will happen for sprint 2 in regard to the home page:
1. Calendar boxes will directly bring user to journal entry. Calendar from sprint 1 is set up (since cells are already generated with onclicklisteners) to easily accomplish this and link to database.
2. Calendar boxes will have mood emojis in background of cell. This can easily be done once database of journal entries are done and when AI parses the emoji.
3. Streak will update based off how many journal entries are made in a row depending on database
### Profile
For sprint 2, we'll also implement profile image upload
