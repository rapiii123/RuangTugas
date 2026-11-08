# 📝 RuangTugas - Student Task Management

**RuangTugas** is a desktop-based application designed to help university students manage academic assignments, monitor deadlines, and organize task progress in one integrated platform.

The application is developed using Java with a modernized user interface to provide a cleaner, more comfortable, and user-friendly experience.

## ✨ Features

- **Dashboard** — Provides an overview and navigation to application features.
- **Task Management** — Add, view, and delete academic assignments.
- **Deadline Management** — Select assignment deadlines using date, month, and year dropdowns.
- **Task Status** — Organize assignments based on their progress:
  - Belum Dikerjakan
  - Sedang Dikerjakan
  - Selesai
- **Database Integration** — Store and retrieve assignment data using MySQL through Hibernate ORM.
- **Modern UI** — Updated interface with a cleaner layout and improved visual experience.

## 🛠️ Technologies

| Technology | Purpose |
|---|---|
| Java | Main programming language |
| Java Swing | Desktop GUI development |
| Hibernate ORM | Object-Relational Mapping |
| MySQL | Relational database |
| Maven | Project and dependency management |
| Visual Studio Code / NetBeans | Development environment |
| Git & GitHub | Version control and collaboration |

## 📂 Project Structure

```text
RuangTugas/
├── src/
│   └── main/
│       ├── java/
│       │   └── ruangtugas/
│       │       ├── HibernateUtil.java
│       │       ├── Koneksi.java
│       │       ├── RuangTugas.java
│       │       └── Tugas.java
│       └── resources/
│           └── hibernate.cfg.xml
├── pom.xml
├── .gitignore
└── README.md
```

## ⚙️ Requirements

Before running the application, make sure the following tools are installed:

- JDK 17 or later
- Apache Maven 3.10.0
- MySQL Server or XAMPP
- Visual Studio Code or NetBeans IDE

## 🛠️ Installation & Setup

**1. Clone the repository**

```bash
git clone https://github.com/rapiii123/RuangTugas.git
```

**2. Open the project**

Open the `RuangTugas` folder using Visual Studio Code or NetBeans.

**3. Create the database**

Start MySQL through XAMPP and create the database:

```sql
CREATE DATABASE ruangtugas;
```

**4. Configure Hibernate**

Open:

```text
src/main/resources/hibernate.cfg.xml
```

Adjust the database connection settings according to your local MySQL configuration, including the database URL, username, and password.

**5. Build the project**

Open the terminal in the project directory and execute:

```bash
mvn clean compile
```

**6. Run the application**

Run the `RuangTugas.java` main class from your IDE.

## 🗄️ Database

RuangTugas uses MySQL as its database and Hibernate ORM to manage object-relational mapping between Java entities and database tables.

Assignment data includes:

| Field | Description |
|---|---|
| id | Unique assignment identifier |
| nama_tugas | Assignment name |
| mata_kuliah | Course name |
| deadline | Assignment deadline |
| status | Assignment progress status |

## 👨‍💻 Developer

**Muhammad Rafi Baihaqi**

Developed as a Java desktop application project to support academic task management.

---

<p align="center">
  <b>RuangTugas</b><br>
  Organize Your Tasks, Manage Your Time.
</p>