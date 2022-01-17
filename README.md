# Passwords and encrypted files manager - web server application
![general1](https://user-images.githubusercontent.com/56108881/149843683-2a042b41-1a40-46cf-b7f9-625fcfbe9d8a.png)

Server application has the following functionalities:
1. Register and authenticate the user;
2. Store user account;
3. Store user’s credentials;
4. Store encryption keys;
5. Establish a friendship between users for file keys sharing;
6. Return the key for a file (if the user has access to it);
7. Make full backup (from the device to the cloud); (to be implemented)
8. Download the latest backup; (to be implemented)

Each endpoint is accessed through the dedicated controller. When the app owner sends a request, the controllers "notify" the underlying Spring MVC, in order to provide the servlets requested. In other words, the server creates a repository for each database entity, whilst JDBC is used for translating these entitites into the actual database. When the client-app sends a request to modify a database object or to retrieve information from it, repositories are checked before the PostgreSQL database comes into play. These requests are handled by Spring Data JPA, Hibernate and, at the lower level, by JDBC, all of them operating with PostgreSQL servers. All in all, Spring Data JPA is used for data persistence (at repository level), while Hibernate is used for database projection (generating queries); lastly, JDBC is for performing CRUD operations on the database.

The authentication and authorization process is managed by Spring Security, along with its best practices. Here is a diagram which explains the process:

![auth2](https://user-images.githubusercontent.com/56108881/149844394-ba94b3d3-b7ec-4837-a53d-9dfdd987972b.jpg)
