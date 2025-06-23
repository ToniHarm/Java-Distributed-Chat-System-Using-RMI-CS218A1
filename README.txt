Group 8
Toni Nam - S11213524 (100%)
Gwen Mar - S11211732  (100%)
Adrian Obadiah - S11198024  (100%)
Adi Filomena Rotuisolia - S11222349  (100%)

# Java Distributed Chat System Using RMI

## Overview

This is a Java Swing-based chat application that allows students to communicate with agents and admins. The application supports role-based messaging, where students can only send private messages to agents and admins, and cannot message other students. Agents and admins can broadcast messages or send private messages to selected users.

## Features

- **Role-based Messaging:** Students can only message agents or admins. Agents and admins can broadcast or send private messages.
- **Message Storage:** All messages, including timestamps, are stored in a MySQL database.
- **User Authentication:** The application supports user login with roles (Student, Agent, Admin).
- **Real-time Communication:** The chat uses Java RMI (Remote Method Invocation) for real-time message passing.

## Prerequisites

- **Java Development Kit (JDK):** Version 8 or higher.
- **MySQL Server:** Installed and running locally or on a remote server.
- **MySQL Connector/J:** Ensure the MySQL JDBC driver is included in your project's dependencies library.
- Run Apache and MySQL on XAMMP 

## Database Setup

1. Install MySQL Server if not already installed.
2. Create a new database:

   ```sql
   CREATE DATABASE user_database;

3. Create tables

-- Table structure for table `chat_messages`
--

CREATE TABLE `chat_messages` (
  `id` int(11) NOT NULL,
  `timeSent` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `sender` varchar(255) NOT NULL,
  `receiver` varchar(255) NOT NULL,
  `message` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `fname` varchar(127) NOT NULL,
  `lname` varchar(127) NOT NULL,
  `username` varchar(127) NOT NULL,
  `password` varchar(127) NOT NULL,
  `security_question` varchar(257) NOT NULL,
  `security_answer` varchar(257) NOT NULL,
  `user_role` varchar(127) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Table structure for table `user_queries`
--

CREATE TABLE `user_queries` (
  `username` varchar(127) NOT NULL,
  `selectedOption` varchar(127) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


4. Run ClientServer
5. Run ChatClient to begin using the application
