# Budget-Brain

## Created By
St10019602 Toby Dwyer St10039352 Brandon Calits

## Youtube Video
https://youtu.be/KGoLOIboaIk

## Overview

Budget-Brain is an Android budgeting application designed to help users manage their finances effectively. The app allows users to track their income, expenses, set savings goals, and analyze their spending habits through various features.

## Features

- **User Authentication:** Users can sign up and log in using email and password or through Google Single Sign-On (SSO).
- **Budget Tracking:** Create and manage budgets for different categories.
- **Transaction Management:** Add, view, and edit transactions linked to specific budgets.
- **Data Visualization:** Analyze spending patterns through charts and graphs using the MPAndroidChart library.
- **User Notifications:** Receive important updates and reminders about account changes and events.

## Technologies Used

- **Programming Language:** Kotlin
- **Architecture:** MVVM (Model-View-ViewModel)
- **Libraries:** 
  - Retrofit for API calls
  - MPAndroidChart for data visualization
  - AndroidX for UI components
- **Database:** Firebase (for user authentication and data storage)

## Getting Started

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/budget-brain.git
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Set up the necessary API keys and configurations in the `gradle.properties` file.
5. Run the app on an emulator or physical device.

### Usage

- **Login:** Enter your credentials or use Google SSO to log in.
- **Dashboard:** View your budgets and transactions.
- **Add Transaction:** Use the floating action button to add new transactions.
- **Budget Management:** Create and manage budgets in the app settings.

## Testing

Automated tests are included for the login functionality. You can run the tests using the Android Studio testing interface or through Gradle:

```bash
./gradlew connectedAndroidTest
