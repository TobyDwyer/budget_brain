# Budget-Brain

## Created By
St10019602 Toby Dwyer St10039352 Brandon Calits

# BudgetBrain

**BudgetBrain** is a personal finance management application designed to help users track their budgets, savings goals, and spending habits. The app offers a seamless user experience with features like Google Single Sign-On (SSO), biometric authentication, and multi-language support.

## Features

- **User Authentication**: Users can register and log in using email/password or Google SSO. Biometric authentication is also supported for enhanced security.
- **User Session Management**: User details are maintained in a session, allowing for a personalized experience.
- **Multi-Language Support**: The application supports multiple languages, with initial support for English and Afrikaans.
- **Budget Tracking**: Users can set savings goals and track their progress towards these goals.
- **Notifications**: Users receive notifications related to their budget and financial goals.
- **Offline Support**: The app is designed to work offline, allowing users to access their data even without an internet connection.

## Technologies Used

- **Android**: The application is built using Kotlin for Android development.
- **Firebase**: Firebase Authentication for user management and Firebase Cloud Messaging for notifications.
- **Retrofit**: Networking library for API calls.
- **Room**: Local database for offline data persistence.
- **Google**: Allows for SSo capabilities in the app
- **Biometric Authentication**: For secure login using device biometrics.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/TobyDwyer/budgetbrain.git
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Set up Firebase in your project and configure the necessary services.
5. Build and run the application on an Android device or emulator.

## Usage

- **Register an Account**: Users can register using their email and password or through Google SSO.
- **Log In**: After registration, users can log in using their credentials or Google SSO.
- **Set Savings Goals**: Users can set and track their savings goals within the app.
- **View Budgets**: Users can view their spending habits and budget allocation.
- **Create Budgets**: Users can create budgets setting their time frame and amount they have to budget.
- **Add Transactions**: Users can add transactions to keep track of spending and link these to specific budgets they can also add it to savings which will track in their savings goal.
- **View Transactions**: Users can vioew a list of all their transactions at any time.
- **Settings**: Users can update their settings and change their language preferences.

## How Was AI used in development:

Ai was used to make redundant task more simple. We used Ai as more of an extra set of hands compared to a full on developer. For tasks such as having multiple language support it was alot easier to ask Ai to extract all the strings from the pages and put them into varibles compared to one of us having to sit and manually make sure we go tevery single peice of text. Although this task is very basic and not hard to do it would have been time consuming and Ai allowed us to focus our efforts into more important areas. The Ai very quickly extracted the text in english  <string name="add_transaction_title">Add Transaction</string> and then also translated the text for us for our other language <string name="add_transaction_title">Voeg Transaksie Toe</string>.  Ai is also used for planning our development. Inputting our scope for the app into the Ai gave us a good overview of what technologies are best to use and the order in which we should go about our development. Thsi helped us be more effcient in our coding and allowed us to give our best for the project. Ai also helps when it comes to bug fixes for issue where the code may not have an error but it wont work. For example for our notifications the code was all correct but we forgot to ask for notification permissions and that is why otifications were not popping up/


