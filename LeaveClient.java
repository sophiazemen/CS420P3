import java.io.*;
import java.net.*;
import java.util.Scanner;

public class LeaveClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 6000);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println(in.readLine());

            while (true) {
                System.out.print("Enter request (Format: <EmployeeID> <LeaveDays>) or type 'exit' to quit: ");
                String request = scanner.nextLine();

                out.println(request);

                if (request.equalsIgnoreCase("exit")) {
                    System.out.println(in.readLine());
                    break;
                }

                System.out.println("Server Response: " + in.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
