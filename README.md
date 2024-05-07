# Telegram bot - shopping list [@reybos_shoping_list_bot](https://t.me/reybos_shoping_list_bot)

<a name="menu"></a>
<ul>
    <li>
        <a href="#about_the_project">About the project</a>
        <ul>
            <li><a href="#bot_features">Bot Features</a></li>
            <li><a href="#technologies">Technologies</a></li>
            <li><a href="#project_structure">Project structure</a></li>
            <li><a href="#db_scheme">Database schema</a></li>
        </ul>
    </li>
</ul>

<h2><a name="about_the_project">About the project</a>&nbsp;&nbsp;<a href="#menu">&#9650;</a></h2>
<h3><a name="bot_features">Bot Features</a>&nbsp;&nbsp;<a href="#menu">&#9650;</a></h3>
<p>
This telegram bot provides the ability to keep a shopping list, and its key feature is the ability to combine the list with other users to maintain a common list.
</p>

- The bot is only available in personal correspondence due to the implementation features.
- Supports two languages: Russian and English, with the ability to change the command.
- A user can send a request to another user to join a group and maintain a single list.
- Confirmation from the list owner is required to join the group.
- The user who accepted the request is considered the owner of the group
- In the future, it is planned to add the ability to remove users from the group and block connection requests.
- Any message sent to the bot will be added to the list
- Limit on the number of characters per message (30 characters) for normal display in the mobile application.
- The list is presented as a message with button elements that allow you to delete items from the list.
- Each member of the group will have one current list, when requesting a new list, the old one is deleted
- When adding a new entry or deleting an old one, the list is automatically updated both in personal correspondence and among the group members.
- If the list is empty, the user is prompted to add an entry by sending the bot any message except commands.
- Basic commands are available in the bot menu, such as displaying the current list, clearing the list, changing the language and requesting another user to join the list.

<p align="center">
  <b>General view</b>
</p>
<p align="center">
  <img src="img/1.png" height="400" title="general view">
</p>

<h3><a name="technologies">Technologies</a>&nbsp;&nbsp;<a href="#menu">&#9650;</a></h3>

<ul>
    <li>Java 17</li>
    <li>Spring Boot</li>
    <li>PostgreSQL</li>
    <li>Maven</li>
    <li><a href="https://github.com/rubenlagus/TelegramBots">Telegram bot library</a></li>
</ul>


<h3><a name="project_structure">Project structure</a>&nbsp;&nbsp;<a href="#menu">&#9650;</a></h3>

<p>
    TODO
</p>

<h3><a name="db_scheme">Database schema</a>&nbsp;&nbsp;<a href="#menu">&#9650;</a></h3>

<p>
    TODO
</p>
