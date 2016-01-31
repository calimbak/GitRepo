# GitRepo
Simple project allowing to research an user on Github and display their repositories

![Alt text](/screenshot/search_portrait.png?raw=true "User list in portrait mode")
![Alt text](/screenshot/repositories_portrait.png?raw=true "Repository list in portrait mode")
![Alt text](/screenshot/landscape.png?raw=true "Landscape mode with dual pane layout")

# Functionnalities
- Endless scroll on ListViews
- Handle orientation change
- Dual pane layout on landscape

# Limitations
- Some users throws errors when asking the listing of the repository [(ex)](https://api.github.com/search/repositories?q=user:torvaldk), errors are just displayed
- The Github API have a [rate limit](https://developer.github.com/v3/#rate-limiting) for unauthenticated users allowing you to make up to 60 requests per hour

# License
See the LICENSE file at the root of the repository.

# Author
Made by Cedric Holtz
