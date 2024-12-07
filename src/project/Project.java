package project;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
public class Project {

    // Graph1 class using adjacency list
    static class Graph1 {
        private int vertices;
        private LinkedList<Edge>[] adjacencyList;
        private int[] weights;

        // Edge class to store destination and weight
        static class Edge {
            int dest;
            int weight;

            Edge(int dest, int weight) {
                this.dest = dest;
                this.weight = weight;
            }
        }

        public Graph1(int v) {
            vertices = v;
            adjacencyList = new LinkedList[v];
            weights = new int[v];
            for (int i = 0; i < v; i++) {
                adjacencyList[i] = new LinkedList<>();
                weights[i] = (int) (Math.random() * 10) + 1; // Random node weights
            }
        }

        public void addEdge(int source, int destination, int weight) {
            adjacencyList[source].add(new Edge(destination, weight));
            adjacencyList[destination].add(new Edge(source, weight)); // Undirected graph
        }

        public LinkedList<Edge>[] getAdjacencyList() {
            return adjacencyList;
        }

        public int getWeight(int node) {
            return weights[node];
        }
    }

    // JPanel to visualize the graph
    static class GraphPanel extends JPanel {

        private Graph1 graph;
        private List<int[]> highlightedEdges = new ArrayList<>();
        private int[] xCoords, yCoords;

        public GraphPanel(Graph1 graph) {
            this.graph = graph;
        }

        public void setHighlightedEdges(List<int[]> edges) {
            this.highlightedEdges = edges;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGraph(g);
        }

        private void drawGraph(Graphics g) {
            int radius = 20; // Radius of the nodes
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
        
            int V = graph.getAdjacencyList().length;
            xCoords = new int[V];
            yCoords = new int[V];
        
            // Set larger font for both node labels and edge weights
            g.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g.getFontMetrics();
        
            // Draw nodes
            for (int i = 0; i < V; i++) {
                xCoords[i] = centerX + (int) (150 * Math.cos(2 * Math.PI * i / V));
                yCoords[i] = centerY + (int) (150 * Math.sin(2 * Math.PI * i / V));
        
                // Draw the node circle
                g.setColor(Color.BLACK);
                g.drawOval(xCoords[i] - radius, yCoords[i] - radius, radius * 2, radius * 2);
                g.setColor(Color.LIGHT_GRAY);
                g.fillOval(xCoords[i] - radius + 1, yCoords[i] - radius + 1, radius * 2 - 1, radius * 2 - 1);
        
                // Draw a contrasting background for the node label
                g.setColor(Color.WHITE);
                int labelWidth = fm.stringWidth(String.valueOf(i));
                int labelHeight = fm.getHeight();
                g.fillOval(xCoords[i] - labelWidth / 2 - 5, yCoords[i] - labelHeight / 2, labelWidth + 10, labelHeight);
        
                // Draw the node number
                g.setColor(Color.BLUE.darker());
                g.drawString(String.valueOf(i), xCoords[i] - labelWidth / 2, yCoords[i] + labelHeight / 4);
            }
        
            // Draw edges with weights
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(2));
            g.setColor(new Color(50, 100, 255)); // Color for edges
            Random rand = new Random();
        
            for (int i = 0; i < V; i++) {
                for (Graph1.Edge edge : graph.getAdjacencyList()[i]) {
                    int neighbor = edge.dest; // Access the destination node of the edge
                    if (i < neighbor) { // Avoid duplicate edges in the undirected graph
                        // Draw edge
                        g2d.drawLine(xCoords[i], yCoords[i], xCoords[neighbor], yCoords[neighbor]);
                
                        // Position the edge weight near the center of the edge
                        int midX = (xCoords[i] + xCoords[neighbor]) / 2;
                        int midY = (xCoords[i] + yCoords[neighbor]) / 2;
                
                        // Draw a contrasting background for the edge weight
                        g.setColor(Color.WHITE);
                        String weightLabel = String.valueOf(edge.weight); // Use the actual weight of the edge
                        int weightWidth = fm.stringWidth(weightLabel);
                        int weightHeight = fm.getHeight();
                        g.fillRect(midX - weightWidth / 2 - 5, midY - weightHeight / 2, weightWidth + 10, weightHeight);
                
                        // Draw the edge weight
                        g.setColor(Color.BLACK);
                        g.drawString(weightLabel, midX - weightWidth / 2, midY + weightHeight / 4);
                    }
                }
                
            }
        
            // Highlight edges (e.g., MST) in red
            g.setColor(Color.RED);
            highlightedEdges.forEach((edge) -> {
                int src = edge[0], dest = edge[1];
                g2d.drawLine(xCoords[src], yCoords[src], xCoords[dest], yCoords[dest]);
                    });
        }
    }

    // GUI for handling graph creation and visualization
    static class GraphAlgorithmsGUI extends JFrame {

        private JTextArea outputArea;
        private JTextField numNodesField;
        private JTextField startNodeField, endNodeField;
        private JComboBox<String> algorithmSelector;
        private Graph1 graph;
        private JPanel graphPanelContainer;

        public GraphAlgorithmsGUI() {
            setTitle("Graph Algorithms and Visualization");
            setSize(800, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            outputArea = new JTextArea();
            outputArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(outputArea);
            add(scrollPane, BorderLayout.SOUTH);

            JPanel inputPanel = new JPanel(new FlowLayout());
            inputPanel.add(new JLabel("Nodes:"));
            numNodesField = new JTextField(5);
            inputPanel.add(numNodesField);

            JButton createGraphButton = new JButton("Create Graph");
            inputPanel.add(createGraphButton);

            inputPanel.add(new JLabel("Algorithm:"));
            algorithmSelector = new JComboBox<>(new String[]{"Shortest Path", "Minimum Spanning Tree"});
            inputPanel.add(algorithmSelector);

            inputPanel.add(new JLabel("Start Node:"));
            startNodeField = new JTextField(5);
            inputPanel.add(startNodeField);

            inputPanel.add(new JLabel("End Node:"));
            endNodeField = new JTextField(5);
            inputPanel.add(endNodeField);

            JButton runAlgorithmsButton = new JButton("Run");
            inputPanel.add(runAlgorithmsButton);

            add(inputPanel, BorderLayout.NORTH);

            graphPanelContainer = new JPanel(new BorderLayout());
            add(graphPanelContainer, BorderLayout.CENTER);

            createGraphButton.addActionListener(e -> createGraph());
            runAlgorithmsButton.addActionListener(e -> runAlgorithms());
        }

        private void createGraph() {
            outputArea.setText("");
            try {
                int V = Integer.parseInt(numNodesField.getText());
                if (V <= 0) {
                    outputArea.append("Invalid number of nodes.\n");
                    return;
                }

                graph = new Graph1(V);
                Random rand = new Random();
                for (int i = 0; i < V; i++) {
                    for (int j = i + 1; j < V; j++) {
                        if (rand.nextBoolean()) {
                            int weight = rand.nextInt(10) + 1;
                            graph.addEdge(i, j, weight);
                        }
                    }
                }

                graphPanelContainer.removeAll();
                GraphPanel graphPanel = new GraphPanel(graph);
                graphPanel.setPreferredSize(new Dimension(600, 400));
                graphPanelContainer.add(graphPanel, BorderLayout.CENTER);
                graphPanelContainer.revalidate();
                graphPanelContainer.repaint();

                outputArea.append("Graph created successfully!\n");
            } catch (NumberFormatException ex) {
                outputArea.append("Invalid input for nodes.\n");
            }
        }

        private void runAlgorithms() {
            if (graph == null) {
                outputArea.append("Create a graph first!\n");
                return;
            }

            String algorithm = (String) algorithmSelector.getSelectedItem();
            try {
                int startNode = Integer.parseInt(startNodeField.getText());
                int endNode = algorithm.equals("Shortest Path") ? Integer.parseInt(endNodeField.getText()) : -1;

                if (startNode < 0 || startNode >= graph.getAdjacencyList().length ||
                        (algorithm.equals("Shortest Path") && (endNode < 0 || endNode >= graph.getAdjacencyList().length))) {
                    outputArea.append("Invalid node inputs.\n");
                    return;
                }

                List<int[]> resultEdges = algorithm.equals("Minimum Spanning Tree")
                        ? primMST(graph)
                        : dijkstra(graph, startNode, endNode);

                GraphPanel graphPanel = (GraphPanel) graphPanelContainer.getComponent(0);
                graphPanel.setHighlightedEdges(resultEdges);

                outputArea.append("Algorithm executed successfully.\n");
            } catch (NumberFormatException ex) {
                outputArea.append("Invalid input for nodes.\n");
            }
        }

        private List<int[]> primMST(Graph1 graph) {
            int V = graph.getAdjacencyList().length;
            PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
            boolean[] inMST = new boolean[V];
            List<int[]> mstEdges = new ArrayList<>();

            pq.add(new int[]{0, -1, 0});
            while (!pq.isEmpty() && mstEdges.size() < V - 1) {
                int[] current = pq.poll();
                int node = current[0], parent = current[1];

                if (inMST[node]) continue;

                inMST[node] = true;
                if (parent != -1) mstEdges.add(new int[]{parent, node});

                for (Graph1.Edge edge : graph.getAdjacencyList()[node]) {
                    if (!inMST[edge.dest]) {
                        pq.add(new int[]{edge.dest, node, edge.weight});
                    }
                }
            }
            return mstEdges;
        }

        private List<int[]> dijkstra(Graph1 graph, int start, int end) {
            int V = graph.getAdjacencyList().length;
        
            // Distance array to hold the shortest distances from the start node
            int[] distances = new int[V];
            Arrays.fill(distances, Integer.MAX_VALUE);
            distances[start] = 0;
        
            // Array to track predecessors for the shortest path reconstruction
            int[] predecessors = new int[V];
            Arrays.fill(predecessors, -1);
        
            // Priority queue to process nodes by their current shortest distance
            PriorityQueue<int[]> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
            priorityQueue.add(new int[]{start, 0});
        
            while (!priorityQueue.isEmpty()) {
                int[] current = priorityQueue.poll();
                int currentNode = current[0];
                int currentDistance = current[1];
        
                // Skip if a shorter path to this node has already been found
                if (currentDistance > distances[currentNode]) continue;
        
                // Explore all adjacent nodes
                for (Graph1.Edge edge : graph.getAdjacencyList()[currentNode]) {
                    int neighbor = edge.dest;
                    int newDistance = currentDistance + edge.weight;
        
                    // Update shortest distance if a better path is found
                    if (newDistance < distances[neighbor]) {
                        distances[neighbor] = newDistance;
                        predecessors[neighbor] = currentNode;
                        priorityQueue.add(new int[]{neighbor, newDistance});
                    }
                }
            }
        
            // Reconstruct the path from the start to the end node
            List<int[]> path = new ArrayList<>();
            for (int at = end; at != -1; at = predecessors[at]) {
                int prev = predecessors[at];
                if (prev != -1) {
                    path.add(new int[]{prev, at});
                }
            }
        
            // Reverse the path since it was built backward
            Collections.reverse(path);
        
            return path;
        }
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GraphAlgorithmsGUI gui = new GraphAlgorithmsGUI();
            gui.setVisible(true);
        });
    }
}
