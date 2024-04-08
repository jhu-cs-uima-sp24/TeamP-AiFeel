# TeamP-AiFeel
You can login with the following info with prepopulated data
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
### Profile
For sprint 2, we'll also implement profile image upload
