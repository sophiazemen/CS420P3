import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class LeaveServer {
    private static final ConcurrentHashMap<String, Employee> employeeData = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        initializeData();

        try (ServerSocket serverSocket = new ServerSocket(6000)) {
            System.out.println("Server started. Listening on port 6000...");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initializeData() {
        employeeData.put("E001", new Employee(20, 20));
        employeeData.put("E002", new Employee(15, 10));
        employeeData.put("E003", new Employee(30, 25));
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                out.println("Welcome to the Leave Management System.");
                
                String request;
                while ((request = in.readLine()) != null) {
                    if (request.equalsIgnoreCase("exit")) {
                        out.println("Connection closed.");
                        break;
                    }

                    String[] parts = request.split(" ");
                    if (parts.length != 2) {
                        out.println("Invalid request. Format: <EmployeeID> <LeaveDays>");
                        continue;
                    }

                    String empId = parts[0];
                    int leaveDays;

                    try {
                        leaveDays = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        out.println("Invalid leave days. Please enter a number.");
                        continue;
                    }

                    Employee employee = employeeData.get(empId);

                    if (employee == null) {
                        out.println("Employee ID not found.");
                    } else if (employee.requestLeave(leaveDays)) {
                        out.println("Approved. Remaining balance: " + employee.getLeaveBalance());
                    } else {
                        out.println("Denied. Insufficient leave balance. Current balance: " + employee.getLeaveBalance());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class Employee {
        private final int entitledLeaves;
        private int leaveBalance;

        public Employee(int entitledLeaves, int leaveBalance) {
            this.entitledLeaves = entitledLeaves;
            this.leaveBalance = leaveBalance;
        }

        public synchronized boolean requestLeave(int leaveDays) {
            if (leaveDays <= leaveBalance) {
                leaveBalance -= leaveDays;
                return true;
            }
            return false;
        }

        public int getLeaveBalance() {
            return leaveBalance;
        }
    }
}
