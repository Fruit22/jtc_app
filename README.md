# RESTful веб-сервис для перевода денег между пользователями/счетами.

### Request URL: http://localhost:8080/transfer
### Method: POST 
### Body content type: application/json

### Пример тела запроса:

```javascript
{
  "senderId": "1",
  "recipientId": "2",
  "amount": "12000.0"
}
```

### Все данные хранятся в in-memory H2 DB
### БД можно управлять на http://localhost:8080/h2-console

