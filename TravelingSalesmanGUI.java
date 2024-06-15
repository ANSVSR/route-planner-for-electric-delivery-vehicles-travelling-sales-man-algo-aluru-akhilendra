import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TravelingSalesmanGUI {
    private JFrame frame;
    private JTextField cityInputField;
    private JTextField rangeInputField;
    private JTextArea resultTextArea;

    public TravelingSalesmanGUI() {
        frame = new JFrame("DELIVERY ROUTE PLANNER");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JLabel cityLabel = new JLabel("Enter the cities you want to visit (space-separated):");
        cityInputField = new JTextField(20);
        JLabel rangeLabel = new JLabel("Enter the vehicle's range:");
        rangeInputField = new JTextField(10);
        JButton solveButton = new JButton("Solve");

        inputPanel.add(cityLabel);
        inputPanel.add(cityInputField);
        inputPanel.add(rangeLabel);
        inputPanel.add(rangeInputField);
        inputPanel.add(solveButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        resultTextArea = new JTextArea(20, 40);
        resultTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);

        frame.add(scrollPane, BorderLayout.CENTER);

        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solveTravelingSalesman();
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    private void solveTravelingSalesman() {
        String citiesInput = cityInputField.getText();
        String rangeInput = rangeInputField.getText();

        // Validate input here if needed

        // Your existing code for solving the problem goes here
        int[][] graph = {
            {0, 29, 50, 70, 60, 80},
            {29, 0, 15, 22, 19, 12},
            {50, 15, 0, 23, 18, 25},
            {70, 22, 23, 0, 17, 28},
            {60, 19, 18, 17, 0, 34},
            {80, 12, 25, 28, 34, 0}
        };

        String[] cityNames = {"adal", "kaz", "hnk", "wgl", "kits", "ursu"};
        int n = graph.length;

        // Adding fuel pumps in "kits" and "ursu" cities
        graph[4][n - 1] = 5; // Fuel pump in "kits"
        graph[n - 1][4] = 5;
        graph[5][n - 1] = 7; // Fuel pump in "ursu"
        graph[n - 1][5] = 7;

        String[] cityTokens = citiesInput.split(" ");
        int[] selectedCities = new int[cityTokens.length];
        boolean validInput = true; // Flag to track if all input cities are valid

        for (int i = 0; i < cityTokens.length; i++) {
            String cityName = cityTokens[i].toLowerCase(); // Convert to lowercase for case-insensitive matching
            int cityIndex = -1;
            for (int j = 0; j < cityNames.length; j++) {
                if (cityNames[j].equals(cityName)) {
                    cityIndex = j;
                    break;
                }
            }

            if (cityIndex == -1) {
                validInput = false;
                resultTextArea.setText("City " + cityName + " is not known.");
                break;
            }

            selectedCities[i] = cityIndex;
        }

        if (!validInput) {
            resultTextArea.append("\nInvalid city input. Exiting.");
            return;
        }

        int vehicleRange = Integer.parseInt(rangeInput);

        int[][] dp = new int[1 << n][n]; // Dynamic programming table
        int[][] parent = new int[1 << n][n]; // To store the parent city for constructing the path

        for (int mask = 0; mask < (1 << n); mask++) {
            for (int u = 0; u < n; u++) {
                dp[mask][u] = Integer.MAX_VALUE / 2; // Initialize with a large value
                parent[mask][u] = -1;
            }
        }

        dp[1][0] = 0; // Starting from city 0

        for (int mask = 1; mask < (1 << n); mask += 2) {
            for (int u = 1; u < n; u++) {
                if ((mask & (1 << u)) != 0) {
                    for (int v = 0; v < n; v++) {
                        if ((mask & (1 << v)) != 0 && v != u) {
                            if (dp[mask ^ (1 << u)][v] + graph[v][u] <= vehicleRange) {
                                if (dp[mask][u] > dp[mask ^ (1 << u)][v] + graph[v][u]) {
                                    dp[mask][u] = dp[mask ^ (1 << u)][v] + graph[v][u];
                                    parent[mask][u] = v;
                                }
                            }
                        }
                    }
                }
            }
        }

        int mask = (1 << n) - 1;
        int u = 0; // Starting city
        int minDistance = Integer.MAX_VALUE;
        int endCity = -1;
        int outOfRangeCity = -1; // To keep track of the city where the vehicle runs out of range

        for (int v = 1; v < n; v++) {
            if (dp[mask][v] + graph[v][0] < minDistance) {
                minDistance = dp[mask][v] + graph[v][0];
                endCity = v;

                if (minDistance > vehicleRange) {
                    outOfRangeCity = v;
                }
            }
        }

        int[] path = new int[n];
        int index = 0;

        if (endCity == -1) {
            resultTextArea.append("\nNo feasible path within the vehicle's range.");
            resultTextArea.append("\nUsing the suboptimal path:");
            while (u != -1) {
                path[index++] = u;
                int prevCity = parent[mask][u];
                mask ^= (1 << u);
                u = prevCity;
            }
        } else {
            while (endCity != -1) {
                path[index++] = endCity;
                int prevCity = parent[mask][endCity];
                mask ^= (1 << endCity);
                endCity = prevCity;
            }
        }

        int totalDistance = calculateDistance(graph, path);

        resultTextArea.append("\nOptimal Path:");
        for (int i = 0; i < selectedCities.length; i++) {
            resultTextArea.append(" -> " + cityNames[path[i]]);
        }
        resultTextArea.append("\nTotal Distance: " + totalDistance);

        if (outOfRangeCity != -1) {
            resultTextArea.append("\nWarning: The vehicle runs out of range at " + cityNames[outOfRangeCity]);
            if (totalDistance > vehicleRange) {
                int nearestFuelPump = (graph[outOfRangeCity][4] <= graph[outOfRangeCity][5]) ? 4 : 5;
                int fuelPumpDistance = graph[outOfRangeCity][nearestFuelPump];
                resultTextArea.append("\nNearest Fuel Pump: " + cityNames[nearestFuelPump] + " (Distance: " + fuelPumpDistance + ")");
            }
        }
    }

    private int calculateDistance(int[][] graph, int[] path) {
        int distance = 0;
        for (int i = 0; i < path.length - 1; i++) {
            distance += graph[path[i]][path[i + 1]];
        }
        distance += graph[path[path.length - 1]][path[0]]; // Return to the starting city
        return distance;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TravelingSalesmanGUI();
            }
        });
    }
}
