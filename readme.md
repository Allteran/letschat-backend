## OkToTalk Project: backend (letschat-backend)
Backend side of chat application for [Team Challenge](teamchallenge.io) - OkToTalk  
### Development tech stack:
- Spring Boot 3.1
- MongoDB as main database
- WebSocket with SimpleBroker + STOMP to transfer messages between users
- Swagger 3.0 (OpenAPI) for documentation  
- Spring Security 4
- User authentication with customized JWT system and OAuth2 with Google

### Deployment:
- Google Cloud App Engine
- MongoDB Atlas

### Documentation with Swagger 3.0
To get access to UI generated by Swagger you should use path ```swagger-doc/swagger-ui/index.html``` 
