# 🌍 Tours and Travel Application
A Java-based web application developed with Spring Boot and Thymeleaf to provide a seamless experience for exploring, booking, and managing tour packages. This project serves as a hands-on example of creating a feature-rich web application with modern tools and technologies.

# 🚀 Features
## 🧳 User Features
### 1. Explore Tours
- View recommended tours with detailed descriptions, itineraries, and prices.
### 2. Book Tours
- Securely book tours directly through the platform.
### 3. Request Custom Packages
- Submit requests for customized tour packages tailored to personal preferences.
### 4. Rate & Review
- Share experiences by leaving reviews and ratings for tours.
## 🛠️ Admin Features
### 1. Dashboard
- View key metrics, including total users, tours, and bookings.
### 2. User Management
- View, update, or delete user accounts.
- Assign admin roles to specific users.
### 3. Booking Management
- Approve or deny tour bookings and custom tour requests.
- Delete unwanted bookings or custom requests.
### 4. Tour Management
- Add new tours to the platform.
- Perform CRUD operations on tours to keep content up-to-date.

# 🛑 Prerequisites
1. Java 17
2. Maven 3.2.3
3. MySQL
4. IDE of your choice (e.g., IntelliJ IDEA, Eclipse, SpringToolSuite (Recommended) )
5. Xampp

# 📂 Project Structure
```plaintext
src  
├── main  
│   ├── java/com/toursandtravel  
│   │   ├── controller  
│   │   ├── initializer  
│   │   ├── model  
│   │   ├── repository  
│   └── resources  
│       ├── static  
│       ├── templates  
│       └── application.properties  
├── test  
└── pom.xml
```

# 💾 Installation & Setup
1. Clone the repository:
```plaintext
git clone https://github.com/yourusername/toursandtravel.git
cd toursandtravel
```
2. Change application.properties.example to application.properties

3. Update application.properties:
   - Configure the database:
     - Create a MySQL database or use an already configured one.
   - Configure for mail:
     - Add your email to spring.mail.username
     - Add your Gmail app password to spring.mail.password:
       - How to get an app password: (https://itsupport.umd.edu/itsupport?id=kb_article_view&sysparm_article=KB0015112)

4. Run XAMPP and start MySQL and Apache

5. Build and run the project:
   - Access the application at: http://localhost:8080

# 🛠️ Technologies Used
- Backend: Spring Boot
- Frontend: Thymeleaf, HTML, CSS, JS, Bootstrap
- Database: MySQL
- API : Jakarta Mail
