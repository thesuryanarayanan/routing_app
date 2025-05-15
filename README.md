# 🚚 Route Optimization Backend

> A powerful Java Spring Boot backend for logistics and delivery route optimization.

This project provides comprehensive solutions for vehicle routing, delivery scheduling, and optimization in logistics operations.

## ✨ Features

- **🗺️ Vehicle Route Optimization**: Advanced VRP (Vehicle Routing Problem) solution using GraphHopper and Jsprit libraries
- **⏰ Delivery Scheduling**: Time-window based delivery optimization for precise planning
- **📍 Geographic Boundaries**: BoundingBox support for delivery zones and territories
- **🔄 RESTful API**: Modern REST API endpoints for all operations
- **🌐 External API Integration**: OpenRouteService integration for detailed route directions

## 🛠️ Technologies

| Technology | Purpose |
|------------|---------|
| Java 17 | Core programming language |
| Spring Boot 3.3.4 | Application framework |
| Spring Data JPA | Data access layer |
| PostgreSQL | Database management |
| Lombok | Boilerplate code reduction |
| GraphHopper (Jsprit) | Route optimization engine |
| Spring Cloud OpenFeign | External API client |

## 🏗️ System Architecture

The project consists of the following components:

### 📊 Entity Classes
- `Branch`: Company branches with geographic coordinates
- `Customer`: Customer information and delivery details
- `Vehicle`: Delivery vehicles with capacity and type
- `Dispatch`: Delivery tasks and assignments
- `DispatchVehicle`: Vehicle-dispatch relationship mapping
- `BoundingBox`: Geographic boundaries for delivery zones

### 💾 Repository Layer
JPA-based data access layer providing basic CRUD operations for all entities.

### ⚙️ Service Layer
Services that manage business logic:
- `BranchService`: Branch management operations
- `CustomerService`: Customer data management
- `VehicleService`: Vehicle fleet management
- `DispatchService`: Delivery task management
- `DispatchVehicleService`: Vehicle-dispatch matching and route optimization

### 🧮 Optimization Modules
- `OptRoutingProblem`: Route problem definition and constraints
- `RoutingProblemSolver`: VRP solver implementation
- `DistanceMatrixService`: Distance calculation between locations

### 🌐 Controller Layer
Controllers providing RESTful APIs:
- `BranchController`: `/branches` endpoints
- `CustomerController`: `/customers` endpoints
- `VehicleController`: `/vehicles` endpoints
- `DispatchController`: `/dispatches` endpoints
- `DispatchVehicleController`: `/dispatch-vehicles` endpoints

## 🚀 Installation

### Prerequisites
- Java 17 or higher
- PostgreSQL database
- Maven

### Setup Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/endmr11/routing_backend.git
   cd routing_backend
   ```

2. **Configure the database:**
   - Create a PostgreSQL database named `routing`
   - Update the database configuration in `application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/routing
       username: [your-database-username]
       password: [your-database-password]
   ```

3. **Build the project:**
   ```bash
   mvn clean install
   ```

4. **Launch the application:**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application:**
   The application will be available at http://localhost:8070

## 📡 API Usage

### Branch Management
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/branches` | GET | List all branches |
| `/branches` | POST | Create a new branch |

### Customer Management
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/customers` | GET | List all customers |
| `/customers` | POST | Create a new customer |

### Vehicle Management
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/vehicles` | GET | List all vehicles |
| `/vehicles/{id}` | GET | Get vehicle by ID |
| `/vehicles` | POST | Create a new vehicle |

### Dispatch Management
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/dispatches` | GET | List all dispatches |
| `/dispatches/{id}` | GET | Get dispatch by ID |
| `/dispatches` | POST | Create a new dispatch |
| `/dispatches/bulk` | POST | Create multiple dispatches |

### Route Optimization
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/dispatch-vehicles` | GET | List all vehicle-dispatch mappings |
| `/dispatch-vehicles/{id}` | GET | Get vehicle-dispatch by ID |
| `/dispatch-vehicles` | POST | Create a new vehicle-dispatch mapping |
| `/dispatch-vehicles/route/{id}` | GET | Calculate optimal route for a specific vehicle-dispatch mapping |

## 🧩 Optimization Algorithm

The system implements a VRP (Vehicle Routing Problem) solution using the GraphHopper Jsprit library. The algorithm considers the following constraints:

- ⏱️ Delivery time windows
- 🗺️ Geographic delivery zones
- 🚛 Vehicle capacities
- 📏 Distance optimization

### Solution Process

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Create Problem │────▶│ Calculate       │────▶│  Solve Using    │
│  Definition     │     │ Distance Matrix │     │  Jsprit Engine  │
└─────────────────┘     └─────────────────┘     └─────────────────┘
         │                                               │
         │                                               ▼
┌─────────────────┐                           ┌─────────────────┐
│  Define         │                           │  Generate       │
│  Constraints    │                           │  Optimal Routes │
└─────────────────┘                           └─────────────────┘
```

## 📄 License

This project is licensed under the MIT License - see the `LICENSE` file for details. 