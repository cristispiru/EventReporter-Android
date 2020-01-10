# EventReporter-Android

The purpose of the application is to display and report catastrophic events.

The app has 4 screens:
- Map Screen: A google map that displays the events using markers. It zooms on the latest added event. The user is able to navigate and zoom on the map. The data for markers is fetched from a server. If the server request fails it loads the data from a local SQLite database. 
- Add Event: The user is able to create a new event. It enters the details and selects the event type from a dropdown list. The values of the dropdown list are fetched from the server. The coordinates for the event are taken from mobiles' GPS coordinates. After the information is completed a POST request is made to the server. 
- Events List: A RecyclerView which displays all the existing events. The events are fetched from the server and loaded in a local SQLite database. If the server request fails, the events are loaded from the local database. When an event is clicked, the EventDetails screen is displayed. 
- Event Details: This screen displays all the information regarding an event.

In order to navigate between screens, the application has drawer navigation.
In order to see the full functionality an android_demo video was added in the bucket.
