# Unictive Spring

Unictive Auth Spring

---

## ✅ Requirements

Ensure you have the following installed:

* Java 17
* Maven
* Docker & Docker Compose
* IntelliJ IDEA (recommended)
* Postman (for API testing)

---

## 🚀 Getting Started

### 🧱 Installation

```bash
# 1. Clone the repository
git clone https://github.com/RezaNurRochmat13/unictive-spring.git
cd unictive-spring

# 2. Build the project
mvn clean install

# 3. Run the application
mvn spring-boot:run
```

Alternatively, you can run with Docker:

```bash
docker-compose up --build
```

---

### 🌐 Access the Application

Once running, open your browser:

```
http://localhost:8080
```

Use Postman or your preferred REST client to interact with the API.

---

## 📦 Tech Stack & Dependencies

* **Spring Boot** – Main framework
* **Spring Data JPA** – ORM & database access
* **PostgreSQL** – Relational database
* **Lombok** – Boilerplate code reduction
* **Docker Compose** – Container orchestration (optional)
* **MapStruct** *(optional)* – Object mapping (add if used)

---

## 🧱 Project Architecture

```
src/
├── presenter/   # Controllers or REST API layer
├── service/     # Business logic layer
├── repository/  # Data access layer (Spring Data JPA)
├── entity/      # JPA Entities
└── config/      # Configuration classes (e.g., DB, CORS, Security)
```

This structure follows a layered architecture and is designed for easy maintainability and testing.

---

## 🤝 Contributing

Contributions are welcome!

1. Fork this repository
2. Create your feature branch: `git checkout -b feature/awesome-feature`
3. Commit your changes: `git commit -m 'Add awesome feature'`
4. Push to the branch: `git push origin feature/awesome-feature`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
