# Deadline

Modify this file to satisfy a submission requirement related to the project
deadline. Please keep this file organized using Markdown. If you click on
this file in your GitHub repository website, then you will see that the
Markdown is transformed into nice-looking HTML.

## Part 1.1: App Description

> Please provide a friendly description of your app, including
> the primary functions available to users of the app. Be sure to
> describe exactly what APIs you are using and how they are connected
> in a meaningful way.

> **Also, include the GitHub `https` URL to your repository.**

My API app allows users to search for a topic or title of a space-related article via the Spaceflight News API (SNAPI for short), and using the date of the retrieved article a call to the TLE API (Two-Line Element API that retrieves info and statistics on orbiting satellites) is made, a satellite launched in the same year the article was published is displayed. The user types in their desired topic or title query, and hits the search button. An article is chosen at random from all eligible articles to be displayed, populating the screen with information like the title, authors, date, summary, URL, and attached image, granted the API response contains all of these fields. When they don't contain all the info, messages are displayed informing the user. From there, a pop-up information box containing information on a random satellite launched in the same year as the article was published will appear. The user can close this information window and repeat the process. An article is randomly selected from a maximum of 200 articles.
https://github.com/tp383923/cs1302-api-app.git

## Part 1.2: APIs

> For each RESTful JSON API that your app uses (at least two are required),
> include an example URL for a typical request made by your app. If you
> need to include additional notes (e.g., regarding API keys or rate
> limits), then you can do that below the URL/URI. Placeholders for this
> information are provided below. If your app uses more than two RESTful
> JSON APIs, then include them with similar formatting.

### Spaceflight News API

```
https://api.spaceflightnewsapi.net/v4/articles/?format=json&limit=200&title_contains=NASA
```

### TLE API

```
https://tle.ivanstanojevic.me/api/tle/?search=2025
```

## Part 2: New

> What is something new and/or exciting that you learned from working
> on this project?

I have truly learned the inner workings of APIs and how they interact with each other and other programs. During Project 4, I didn't understand exactly how the API worked, and I relied on the fact that we already had the code for variables written down and much of the process explained for us. Now I know how to work around variables not matching with API variables, what URL encoded queries are supposed to look like, and more. I have really enjoyed learning about APIs more, and this opens a lot of doors for useful programs.

## Part 3: Retrospect

> If you could start the project over from scratch, what do
> you think might do differently and why?

I would research each API more thoroughly. I had problems that sprung up during my project because I did not know how the parameters and queries for each API worked, but redoing this project, I have a better knowledge about APIs and how they work.
