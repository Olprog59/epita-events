# Events Spring Boot

- GET // param en clair => ne pas depasser 2000 caracteres url | attention
- POST // param ne sont pas en clair => pas de limites
- PUT // update => si attribut manquant => en bdd le champs sera vide
- PATCH // update => n'ecrasera pas les champs vides
- DELETE
- HEAD
- OPTIONS

## RESTFull convention

### Recuperer des informations | toutes les informations

- GET http://localhost:8080/api/events

### Ajout un event

- POST http://localhost:8080/api/events

### Recuperer une information | un evenement

- GET http://localhost:8080/api/events/25

### Mettre a jour mon event 25

- PUT|PATCH http://localhost:8080/api/events/25

### Supprimer un event (25)

- DELETE http://localhost:8080/api/events/25

---

### Recuperer tous les participants d'un event

- GET http://localhost:8080/api/events/25/participants

### Recuperer un participant de cet event

- GET http://localhost:8080/api/events/25/participants/12

---

### filtrage | tri

- GET http://localhost:8080/api/events?tri=asc&prix=0-100
