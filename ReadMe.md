# 🏨 Grand Hotel Management System

A full-featured desktop Hotel Management System built using **Java, JavaFX, Maven, and MySQL** to streamline hotel operations including room management, guest registration, reservations, billing, checkout, and business analytics.

The application provides a modern dashboard-driven experience for hotel staff and managers while maintaining persistent records of rooms, guests, bookings, revenue, and occupancy data.

---

# 🚀 Project Highlights

✅ Multi-role Authentication System

✅ Real-Time Hotel Dashboard

✅ Smart Room Allocation Engine

✅ Dynamic Occupancy Tracking

✅ Guest Registration & Management

✅ Reservation & Booking Workflow

✅ Automated Checkout Processing

✅ Invoice & Billing Generation

✅ Revenue Analytics Dashboard

✅ Search & Filtering Across Modules

✅ Persistent MySQL Database Storage

✅ Modern Dark-Themed JavaFX Interface

---

# 📖 Project Overview

Managing hotel operations manually becomes increasingly difficult as the number of rooms, guests, reservations, and transactions grows.

Grand Hotel Management System solves this problem by providing a centralized platform where hotel staff can:

* Monitor room occupancy
* Register guests
* Create bookings
* Track active stays
* Process checkouts
* Generate invoices
* Analyze hotel performance
* View revenue insights

All data is stored permanently in a MySQL database and updated across modules in real time.

---

# 🔐 Authentication System

The system supports three user roles:

### Manager

Full system access.

### Front Desk

Handles guest registrations, bookings, and checkout operations.

### Staff

Operational access for day-to-day hotel management tasks.

### Features

* Role selection buttons
* Auto-fill login credentials
* Email validation
* Gmail-only authentication format validation
* Secure role-based entry into the application

---

# 📊 Dashboard Module

The dashboard acts as the operational control center of the hotel.

### Real-Time Statistics

* Total Rooms
* Available Rooms
* Occupied Rooms
* Daily Revenue
* Pending Checkouts
* Occupancy Percentage

### Smart Features

* Dynamic greeting system

  * Good Morning
  * Good Afternoon
  * Good Evening

* Live digital clock

* Floor-wise room visualization

* Active guest monitoring

* Recent activity tracking

* Revenue summary cards

* Quick navigation actions

The dashboard updates automatically as bookings, room allocations, and checkouts occur.

---

# 🏨 Room Management System

The room management module allows hotel administrators to manage all hotel rooms from a single interface.

### Room Information

Each room stores:

* Room Number
* Room Type
* Floor Number
* Price Per Day
* Current Status

### Room Status Types

* Available
* Occupied
* Cleaning
* Maintenance

### Features

* Add new rooms
* Update room information
* Delete rooms
* Change room status
* Search rooms instantly
* Filter rooms by status
* Visual room card previews

Selecting a room automatically loads all associated information for editing.

---

# 👥 Guest Management

The guest management module maintains complete customer records.

### Features

* Register guests
* Store contact details
* Manage room assignments
* View guest history
* Search guest records
* Filter active and checked-out guests

### Smart Allocation Logic

Only rooms marked as:

```text
Available
```

can be assigned to guests.

Once a guest is assigned:

```text
Available → Occupied
```

This prevents double bookings and ensures room availability remains accurate throughout the system.

---

# 📅 Booking Management

The booking module handles reservations and stay planning.

### Booking Workflow

1. Select customer
2. Choose available room
3. Select check-in date
4. Enter duration of stay
5. Calculate checkout date
6. Generate booking summary
7. Confirm booking

### Automated Features

* Room availability validation
* Stay duration calculation
* Checkout date generation
* Total amount calculation
* Booking record generation

Only available rooms are displayed during reservation creation.

---

# 💳 Checkout & Billing System

The checkout module manages guest departures and payment processing.

### Features

* Active guest selection
* Booking summary generation
* Payment processing
* Invoice generation
* Printable receipts
* PDF export support

### Supported Payment Methods

* Cash
* Card
* UPI

### Automated Checkout Logic

After checkout completion:

```text
Occupied → Available
```

The room instantly becomes available for future bookings.

This ensures occupancy statistics remain accurate without manual updates.

---

# 📈 Reports & Analytics

The analytics module provides business intelligence and operational insights.

### Key Metrics

* Total Revenue
* Occupancy Rate
* Average Stay Duration
* Revenue Per Room
* Room Performance Ranking

### Visual Analytics

* Weekly Revenue Charts
* Revenue Distribution
* Occupancy Analysis
* Booking Trends
* Room Type Performance

### Date Range Filters

Reports can be generated for:

* Weekly
* Monthly
* Custom Date Range

### Export Features

Reports can be exported directly for management review.

---

# 🧠 Business Rules Implemented

### No Double Booking

Two guests cannot occupy the same room simultaneously.

### Availability Validation

Only available rooms can be booked.

### Automatic Status Updates

Booking:

```text
Available → Occupied
```

Checkout:

```text
Occupied → Available
```

### Persistent Data Storage

All operations are stored inside MySQL and remain available after application restart.

---

# 🛠 Technology Stack

| Component    | Technology              |
| ------------ | ----------------------- |
| Language     | Java                    |
| UI Framework | JavaFX                  |
| Database     | MySQL                   |
| Build Tool   | Maven                   |
| Architecture | Desktop Application     |
| IDE          | VS Code / IntelliJ IDEA |

---

# 📂 Core Modules

* Authentication System
* Dashboard
* Room Management
* Guest Management
* Booking System
* Checkout & Billing
* Reporting & Analytics

---

# 📸 Screenshots

## Login Screen

(Add Screenshot)

## Dashboard

(Add Screenshot)

## Room Management

(Add Screenshot)

## Guest Management

(Add Screenshot)

## Booking System

(Add Screenshot)

## Checkout & Billing

(Add Screenshot)

## Reports & Analytics

(Add Screenshot)

---

# 🔮 Future Improvements

* Online Guest Reservations
* QR-Based Check-In
* Email Notifications
* SMS Alerts
* Multi-Hotel Management
* Employee Attendance Module
* AI-Based Revenue Forecasting
* Cloud Database Integration

---

# 👩‍💻 Author

**Tarunashri Surapaneni**

Data Science & Engineering
Manipal Institute of Technology

GitHub: https://github.com/tarunashri6

---

⭐ If you found this project interesting, consider giving it a star.

