import javax.swing.*;
import java.awt.*;
/*import java.awt.event.*;*/
import java.nio.ByteBuffer;
import java.util.*;
import java.io.*;

public class BufferOverflowDetector extends JFrame {
    private JTabbedPane tabbedPane;
    private JTextArea outputArea;

    public BufferOverflowDetector() {
        setTitle("Détecteur de Dépassement de Tampon");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the main UI components
        tabbedPane = new JTabbedPane();
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputArea);

        // Create each tab for the different types of buffer overflow tests
        tabbedPane.addTab("Chaînes de Caractères", createStringPanel());
        tabbedPane.addTab("Types Numériques", createNumericPanel());
        tabbedPane.addTab("Tableaux et Matrices", createArrayPanel());
        tabbedPane.addTab("Listes et Piles", createCollectionsPanel());
        tabbedPane.addTab("Mémoire", createMemoryPanel());

        // Set up the overall layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
                                             tabbedPane, outputScrollPane);
        splitPane.setDividerLocation(350);
        add(splitPane);

        // Add a clear button for the output area
        JButton clearButton = new JButton("Effacer la sortie");
        clearButton.addActionListener(e -> outputArea.setText(""));
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(outputScrollPane, BorderLayout.CENTER);
        bottomPanel.add(clearButton, BorderLayout.SOUTH);

        splitPane.setBottomComponent(bottomPanel);
    }

    private JPanel createStringPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Buffer Size Control
        JLabel bufferSizeLabel = new JLabel("Taille du tampon:");
        JSpinner bufferSizeSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        
        // Input Control
        JLabel inputLabel = new JLabel("Entrée:");
        JTextField inputField = new JTextField(20);
        
        // Test Button
        JButton testButton = new JButton("Tester le dépassement");
        testButton.addActionListener(e -> {
            int bufferSize = (Integer) bufferSizeSpinner.getValue();
            String input = inputField.getText();
            testStringBufferOverflow(bufferSize, input);
        });

        // Add explanation
        JTextArea explanationArea = new JTextArea(
            "Ce test simule un dépassement de tampon avec des chaînes de caractères.\n" +
            "Spécifiez une taille de tampon et saisissez une chaîne. Si la chaîne dépasse\n" +
            "la taille du tampon, un dépassement est détecté."
        );
        explanationArea.setEditable(false);
        explanationArea.setBackground(null);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setLineWrap(true);

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(bufferSizeLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(bufferSizeSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(inputLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(inputField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        controlPanel.add(testButton, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        controlPanel.add(explanationArea, gbc);

        panel.add(controlPanel, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createNumericPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Type Selection
        JLabel typeLabel = new JLabel("Type de donnée:");
        String[] types = {"byte", "short", "int", "long", "float", "double"};
        JComboBox<String> typeComboBox = new JComboBox<>(types);
        
        // Value Input
        JLabel valueLabel = new JLabel("Valeur à tester:");
        JTextField valueField = new JTextField(20);
        
        // Test Button
        JButton testButton = new JButton("Tester le dépassement");
        testButton.addActionListener(e -> {
            String selectedType = (String) typeComboBox.getSelectedItem();
            String value = valueField.getText();
            testNumericBufferOverflow(selectedType, value);
        });

        // Explanation
        JTextArea explanationArea = new JTextArea(
            "Ce test vérifie les dépassements pour différents types numériques.\n" +
            "Saisissez une valeur et sélectionnez le type cible pour détecter\n" +
            "les dépassements lors de la conversion.\n"
        );
        explanationArea.setEditable(false);
        explanationArea.setBackground(null);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setLineWrap(true);

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(typeLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(typeComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(valueLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(valueField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        controlPanel.add(testButton, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        controlPanel.add(explanationArea, gbc);

        panel.add(controlPanel, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createArrayPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Array Size Control
        JLabel sizeLabel = new JLabel("Taille du tableau:");
        JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        
        // Index Control
        JLabel indexLabel = new JLabel("Index à accéder:");
        JSpinner indexSpinner = new JSpinner(new SpinnerNumberModel(0, -10, 200, 1));
        
        // Dimension Selection
        JLabel dimensionLabel = new JLabel("Dimension:");
        String[] dimensions = {"Tableau 1D", "Matrice 2D"};
        JComboBox<String> dimensionComboBox = new JComboBox<>(dimensions);
        
        // Test Button
        JButton testButton = new JButton("Tester l'accès au tableau");
        testButton.addActionListener(e -> {
            int size = (Integer) sizeSpinner.getValue();
            int index = (Integer) indexSpinner.getValue();
            boolean is2D = dimensionComboBox.getSelectedIndex() == 1;
            testArrayAccess(size, index, is2D);
        });

        // Explanation
        JTextArea explanationArea = new JTextArea(
            "Ce test simule un accès hors limites dans des tableaux.\n" +
            "Définissez une taille de tableau et tentez d'accéder à différents indices\n" +
            "pour observer les comportements de dépassement.\n"
        );
        explanationArea.setEditable(false);
        explanationArea.setBackground(null);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setLineWrap(true);

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(sizeLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(sizeSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(indexLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(indexSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        controlPanel.add(dimensionLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(dimensionComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        controlPanel.add(testButton, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        controlPanel.add(explanationArea, gbc);

        panel.add(controlPanel, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createCollectionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Collection Type
        JLabel typeLabel = new JLabel("Type de collection:");
        String[] types = {"ArrayList", "LinkedList", "Stack", "Vector"};
        JComboBox<String> typeComboBox = new JComboBox<>(types);
        
        // Capacity/Size
        JLabel capacityLabel = new JLabel("Capacité initiale:");
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        
        // Number of Elements to Add
        JLabel elementsLabel = new JLabel("Éléments à ajouter:");
        JSpinner elementsSpinner = new JSpinner(new SpinnerNumberModel(15, 1, 1000, 1));
        
        // Test Button
        JButton testButton = new JButton("Tester la collection");
        testButton.addActionListener(e -> {
            String collectionType = (String) typeComboBox.getSelectedItem();
            int capacity = (Integer) capacitySpinner.getValue();
            int elementsToAdd = (Integer) elementsSpinner.getValue();
            testCollection(collectionType, capacity, elementsToAdd);
        });

        // Explanation
        JTextArea explanationArea = new JTextArea(
            "Ce test explore les limites des collections Java.\n" +
            "Définissez une capacité initiale et tentez d'ajouter plus d'éléments\n" +
            "pour observer comment différentes collections gèrent les dépassements.\n"
        );
        explanationArea.setEditable(false);
        explanationArea.setBackground(null);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setLineWrap(true);

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(typeLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(typeComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(capacityLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(capacitySpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        controlPanel.add(elementsLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(elementsSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        controlPanel.add(testButton, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        controlPanel.add(explanationArea, gbc);

        panel.add(controlPanel, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createMemoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Memory Type
        JLabel typeLabel = new JLabel("Type de mémoire:");
        String[] types = {"Mémoire centrale (RAM)", "Fichier disque", "ByteBuffer"};
        JComboBox<String> typeComboBox = new JComboBox<>(types);
        
        // Size allocation
        JLabel sizeLabel = new JLabel("Taille d'allocation (MB):");
        JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        
        // Test Button
        JButton testButton = new JButton("Tester l'allocation mémoire");
        testButton.addActionListener(e -> {
            String memoryType = (String) typeComboBox.getSelectedItem();
            int sizeMB = (Integer) sizeSpinner.getValue();
            testMemoryAllocation(memoryType, sizeMB);
        });
        
        // Heap Memory Info Button
        JButton heapInfoButton = new JButton("Informations mémoire JVM");
        heapInfoButton.addActionListener(e -> showHeapInfo());

        // Explanation
        JTextArea explanationArea = new JTextArea(
            "Ce test examine les limites de différents types de mémoire.\n" +
            "Spécifiez un type de mémoire et tentez d'allouer différentes quantités\n" +
            "pour observer les comportements en cas de dépassement.\n"
        );
        explanationArea.setEditable(false);
        explanationArea.setBackground(null);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setLineWrap(true);

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(typeLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(typeComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(sizeLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(sizeSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        controlPanel.add(testButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        controlPanel.add(heapInfoButton, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        controlPanel.add(explanationArea, gbc);

        panel.add(controlPanel, BorderLayout.NORTH);
        return panel;
    }

    // Test implementations

    private void testStringBufferOverflow(int bufferSize, String input) {
        logOutput("\n--- Test de dépassement de tampon pour chaînes ---");
        logOutput("- Taille du tampon: " + bufferSize);
        logOutput("- Longueur de l'entrée: " + input.length());
        logOutput("- Entrée: " + input);
        
        char[] buffer = new char[bufferSize];
        try {
            // Simulated unsafe copy (like C's strcpy)
            for (int i = 0; i < input.length(); i++) {
                buffer[i] = input.charAt(i);  // This will throw exception if overflow
            }
            logOutput("✓ Pas de dépassement détecté. La chaîne a été copiée avec succès.");
        } catch (ArrayIndexOutOfBoundsException e) {
            int overflowAmount = input.length() - bufferSize;
            logOutput(" DÉPASSEMENT DÉTECTÉ! La chaîne dépasse le tampon de " + overflowAmount + " caractères.");
            
            // Show visualization of what happened
            StringBuilder visualization = new StringBuilder();
            visualization.append("Buffer: [");
            for (int i = 0; i < bufferSize; i++) {
                if (i < input.length()) {
                    visualization.append(input.charAt(i));
                } else {
                    visualization.append("_");
                }
                if (i < bufferSize - 1) visualization.append("|");
            }
            visualization.append("]\n");
            
            // Show overflow
            if (overflowAmount > 0) {
                visualization.append("Overflow: [");
                for (int i = bufferSize; i < input.length(); i++) {
                    visualization.append(input.charAt(i));
                    if (i < input.length() - 1) visualization.append("|");
                }
                visualization.append("]");
            }
            
            logOutput(visualization.toString());
            logOutput("Cette situation en C pourrait corrompre la pile et permettre une exécution de code arbitraire.");
        }
    }

    private void testNumericBufferOverflow(String type, String valueStr) {
        logOutput("\n--- Test de dépassement pour types numériques ---");
        logOutput("- Type ciblé: " + type);
        logOutput("- Valeur testée: " + valueStr);
        
        try {
            switch (type) {
                case "byte":
                    try {
                        long value = Long.parseLong(valueStr);
                        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
                            logOutput(" DÉPASSEMENT DÉTECTÉ! La valeur " + value + 
                                     " est hors des limites d'un byte (" + Byte.MIN_VALUE + 
                                     " à " + Byte.MAX_VALUE + ")");
                            byte converted = (byte) value;
                            logOutput("Conversion forcée: " + converted + 
                                     " (perte de données par troncature)");
                        } else {
                            byte converted = (byte) value;
                            logOutput("✓ Pas de dépassement. Conversion réussie: " + converted);
                        }
                    } catch (NumberFormatException e) {
                        logOutput(" Format invalide pour un nombre.");
                    }
                    break;
                    
                case "short":
                    try {
                        long value = Long.parseLong(valueStr);
                        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
                            logOutput(" DÉPASSEMENT DÉTECTÉ! La valeur " + value + 
                                     " est hors des limites d'un short (" + Short.MIN_VALUE + 
                                     " à " + Short.MAX_VALUE + ")");
                            short converted = (short) value;
                            logOutput("Conversion forcée: " + converted + 
                                     " (perte de données par troncature)");
                        } else {
                            short converted = (short) value;
                            logOutput("✓ Pas de dépassement. Conversion réussie: " + converted);
                        }
                    } catch (NumberFormatException e) {
                        logOutput(" Format invalide pour un nombre.");
                    }
                    break;
                    
                case "int":
                    try {
                        long value = Long.parseLong(valueStr);
                        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                            logOutput(" DÉPASSEMENT DÉTECTÉ! La valeur " + value + 
                                     " est hors des limites d'un int (" + Integer.MIN_VALUE + 
                                     " à " + Integer.MAX_VALUE + ")");
                            int converted = (int) value;
                            logOutput("Conversion forcée: " + converted + 
                                     " (perte de données par troncature)");
                        } else {
                            int converted = (int) value;
                            logOutput("✓ Pas de dépassement. Conversion réussie: " + converted);
                        }
                    } catch (NumberFormatException e) {
                        logOutput(" Format invalide pour un nombre.");
                    }
                    break;
                    
                case "long":
                    try {
                        double value = Double.parseDouble(valueStr);
                        if (value < Long.MIN_VALUE || value > Long.MAX_VALUE) {
                            logOutput(" DÉPASSEMENT DÉTECTÉ! La valeur " + value + 
                                     " est hors des limites d'un long (" + Long.MIN_VALUE + 
                                     " à " + Long.MAX_VALUE + ")");
                            long converted = (long) value;
                            logOutput("Conversion forcée: " + converted + 
                                     " (perte de données par troncature)");
                        } else {
                            long converted = (long) value;
                            logOutput("✓ Pas de dépassement. Conversion réussie: " + converted);
                        }
                    } catch (NumberFormatException e) {
                        logOutput(" Format invalide pour un nombre.");
                    }
                    break;
                    
                case "float":
                    try {
                        double value = Double.parseDouble(valueStr);
                        if (Math.abs(value) > Float.MAX_VALUE) {
                            logOutput(" DÉPASSEMENT DÉTECTÉ! La valeur absolue " + Math.abs(value) + 
                                     " est supérieure à Float.MAX_VALUE (" + Float.MAX_VALUE + ")");
                            float converted = (float) value;
                            logOutput("Conversion forcée: " + converted + 
                                     (Float.isInfinite(converted) ? " (INFINITY)" : ""));
                        } else {
                            float converted = (float) value;
                            logOutput("✓ Pas de dépassement. Conversion réussie: " + converted);
                        }
                    } catch (NumberFormatException e) {
                        logOutput(" Format invalide pour un nombre.");
                    }
                    break;
                    
                case "double":
                    try {
                        double value = Double.parseDouble(valueStr);
                        if (Double.isInfinite(value)) {
                            logOutput(" DÉPASSEMENT DÉTECTÉ! La valeur dépasse les limites du type double.");
                            logOutput("Double.MAX_VALUE: " + Double.MAX_VALUE);
                        } else {
                            logOutput("✓ Pas de dépassement. Valeur acceptée: " + value);
                        }
                    } catch (NumberFormatException e) {
                        logOutput(" Format invalide pour un nombre.");
                    }
                    break;
            }
        } catch (Exception e) {
            logOutput(" Erreur lors du test: " + e.getMessage());
        }
    }

    private void testArrayAccess(int size, int index, boolean is2D) {
        if (!is2D) {
            // 1D Array Test
            logOutput("\n--- Test d'accès à un tableau 1D ---");
            logOutput("- Taille du tableau: " + size);
            logOutput("- Index tenté: " + index);
            
            int[] array = new int[size];
            try {
                // Initialize array
                for (int i = 0; i < size; i++) {
                    array[i] = i * 10;
                }
                
                // Try to access with specified index
                int value = array[index];
                logOutput("✓ Accès réussi à l'index " + index + ": " + value);
            } catch (ArrayIndexOutOfBoundsException e) {
                logOutput(" DÉPASSEMENT DÉTECTÉ! L'index " + index + 
                         " est hors des limites du tableau [0-" + (size-1) + "]");
                
                // Visualization
                StringBuilder visual = new StringBuilder();
                visual.append("Indexes: ");
                for (int i = 0; i < size; i++) {
                    visual.append(String.format("%3d ", i));
                }
                visual.append("\nValues:  ");
                for (int i = 0; i < size; i++) {
                    visual.append(String.format("%3d ", array[i]));
                }
                visual.append("\n");
                
                if (index < 0) {
                    visual.append("Tentative d'accès à l'index négatif " + index + " (avant le début du tableau)");
                } else {
                    visual.append("Tentative d'accès à l'index " + index + " (après la fin du tableau)");
                }
                
                logOutput(visual.toString());
            }
        } else {
            // 2D Matrix Test
            logOutput("\n--- Test d'accès à une matrice 2D ---");
            int matrixSize = (int) Math.sqrt(size);  // Make it a square matrix
            if (matrixSize < 2) matrixSize = 2;  // Minimum size
            
            logOutput("- Taille de la matrice: " + matrixSize + "x" + matrixSize);
            logOutput("- Index ligne/colonne tenté: " + (index / matrixSize) + "/" + (index % matrixSize));
            
            int[][] matrix = new int[matrixSize][matrixSize];
            try {
                // Initialize matrix
                for (int i = 0; i < matrixSize; i++) {
                    for (int j = 0; j < matrixSize; j++) {
                        matrix[i][j] = i * 10 + j;
                    }
                }
                
                // Try to access with specified indexes
                int row = index / matrixSize;
                int col = index % matrixSize;
                int value = matrix[row][col];
                logOutput("✓ Accès réussi à l'index [" + row + "][" + col + "]: " + value);
            } catch (ArrayIndexOutOfBoundsException e) {
                int row = index / matrixSize;
                int col = index % matrixSize;
                logOutput(" DÉPASSEMENT DÉTECTÉ! L'index [" + row + "][" + col + 
                         "] est hors des limites de la matrice [0-" + (matrixSize-1) + "][0-" + (matrixSize-1) + "]");
                
                // Visualization of the matrix
                StringBuilder visual = new StringBuilder();
                visual.append("Matrice " + matrixSize + "x" + matrixSize + ":\n");
                
                // Column headers
                visual.append("    ");
                for (int j = 0; j < matrixSize; j++) {
                    visual.append(String.format("%3d ", j));
                }
                visual.append("\n");
                
                // Matrix with row headers
                for (int i = 0; i < matrixSize; i++) {
                    visual.append(String.format("%3d ", i));
                    for (int j = 0; j < matrixSize; j++) {
                        visual.append(String.format("%3d ", matrix[i][j]));
                    }
                    visual.append("\n");
                }
                
                if (row < 0 || col < 0) {
                    visual.append("Tentative d'accès à un index négatif [" + row + "][" + col + "]");
                } else if (row >= matrixSize) {
                    visual.append("Tentative d'accès à une ligne au-delà de la matrice [" + row + "]");
                } else {
                    visual.append("Tentative d'accès à une colonne au-delà de la matrice [" + row + "][" + col + "]");
                }
                
                logOutput(visual.toString());
            }
        }
    }

    private void testCollection(String collectionType, int capacity, int elementsToAdd) {
        logOutput("\n--- Test de collection ---");
        logOutput("- Type de collection: " + collectionType);
        logOutput("- Capacité initiale: " + capacity);
        logOutput("- Éléments à ajouter: " + elementsToAdd);
        
        try {
            switch (collectionType) {
                case "ArrayList":
                    ArrayList<Integer> arrayList = new ArrayList<>(capacity);
                    logOutput("Collection ArrayList créée avec capacité " + capacity);
                    
                    // Add elements
                    for (int i = 0; i < elementsToAdd; i++) {
                        arrayList.add(i);
                    }
                    
                    logOutput("✓ " + elementsToAdd + " éléments ajoutés avec succès.");
                    logOutput("- Taille finale: " + arrayList.size());
                    logOutput("- Capacité initiale: " + capacity);
                    
                    if (elementsToAdd > capacity) {
                        logOutput(" NOTE: ArrayList a automatiquement redimensionné sa capacité interne.");
                        logOutput("- En Java, ArrayList gère automatiquement sa capacité et ne cause pas de dépassement de tampon.");
                        logOutput("- Pour comparaison, en C, ce serait un dépassement de tampon classique.");
                    }
                    break;
                    
                    case "LinkedList":
                    LinkedList<Integer> linkedList = new LinkedList<>();
                    logOutput("Collection LinkedList créée");
                    
                    // Add elements
                    for (int i = 0; i < elementsToAdd; i++) {
                        linkedList.add(i);
                    }
                    
                    logOutput("✓ " + elementsToAdd + " éléments ajoutés avec succès.");
                    logOutput("- Taille finale: " + linkedList.size());
                    logOutput("- LinkedList n'a pas de capacité fixe (structure basée sur des nœuds chaînés)");
                    logOutput("- Les LinkedList ne peuvent pas avoir de dépassement de capacité en Java.");
                    break;
                    
                case "Stack":
                    // Java's Stack doesn't take an initial capacity in constructor directly
                    // Stack extends Vector which has default capacity of 10
                    Stack<Integer> stack = new Stack<>();
                    logOutput("Collection Stack créée (capacité par défaut d'un Vector en Java: 10)");
                    
                    // Add elements
                    for (int i = 0; i < elementsToAdd; i++) {
                        stack.push(i);
                    }
                    
                    logOutput("✓ " + elementsToAdd + " éléments empilés avec succès.");
                    logOutput("- Taille finale: " + stack.size());
                    
                    if (elementsToAdd > 10) {
                        logOutput(" NOTE: Stack (basé sur Vector) a automatiquement redimensionné sa capacité interne.");
                        logOutput("- En Java, Stack gère automatiquement sa capacité.");
                        logOutput("- Pour comparaison, en C, une pile de taille fixe provoquerait un dépassement.");
                    }
                    break;
                    
                case "Vector":
                    Vector<Integer> vector = new Vector<>(capacity);
                    logOutput("Collection Vector créée avec capacité " + capacity);
                    
                    // Add elements
                    for (int i = 0; i < elementsToAdd; i++) {
                        vector.add(i);
                    }
                    
                    logOutput("✓ " + elementsToAdd + " éléments ajoutés avec succès.");
                    logOutput("- Taille finale: " + vector.size());
                    logOutput("- Capacité initiale: " + capacity);
                    
                    if (elementsToAdd > capacity) {
                        logOutput(" NOTE: Vector a automatiquement redimensionné sa capacité interne.");
                        logOutput("- En Java, Vector gère automatiquement sa capacité et ne cause pas de dépassement de tampon.");
                        logOutput("- Pour comparaison, en C, ce serait un dépassement de tampon classique.");
                    }
                    break;
            }
            
            logOutput("En Java, les collections sont sécurisées contre les dépassements de tampon");
            logOutput("contrairement aux tableaux en C où un dépassement peut écraser la mémoire adjacente.");
            
        } catch (Exception e) {
            logOutput(" Erreur lors du test: " + e.getMessage());
        }
    }

    private void testMemoryAllocation(String memoryType, int sizeMB) {
        logOutput("\n--- Test d'allocation mémoire ---");
        logOutput("- Type de mémoire: " + memoryType);
        logOutput("- Taille demandée: " + sizeMB + " MB");
        
        // Convert MB to bytes
        long sizeBytes = sizeMB * 1024L * 1024L;
        
        try {
            switch (memoryType) {
                case "Mémoire centrale (RAM)":
                    logOutput("Tentative d'allocation d'un tableau de " + sizeMB + " MB en RAM...");
                    try {
                        byte[] largeArray = new byte[(int)sizeBytes];
                        logOutput("✓ Allocation réussie de " + sizeMB + " MB en RAM.");
                        
                        // Fill with some data to ensure memory is actually allocated
                        for (int i = 0; i < Math.min(1000, largeArray.length); i++) {
                            largeArray[i] = (byte)(i % 256);
                        }
                        
                        // Garbage collect to free memory
                        largeArray = null;
                        System.gc();
                        
                    } catch (OutOfMemoryError e) {
                        logOutput(" DÉPASSEMENT DÉTECTÉ! Impossible d'allouer " + sizeMB + " MB en RAM.");
                        logOutput("- Erreur: " + e.getMessage());
                        logOutput("- Ceci est un dépassement de la mémoire disponible pour la JVM.");
                        
                        // Show available memory
                        Runtime runtime = Runtime.getRuntime();
                        long maxMemory = runtime.maxMemory() / (1024 * 1024);
                        long freeMemory = runtime.freeMemory() / (1024 * 1024);
                        logOutput("- Mémoire maximale JVM: " + maxMemory + " MB");
                        logOutput("- Mémoire libre JVM: " + freeMemory + " MB");
                    }
                    break;
                    
                case "Fichier disque":
                    logOutput("Tentative d'écriture d'un fichier de " + sizeMB + " MB sur disque...");
                    File tempFile = null;
                    FileOutputStream fos = null;
                    
                    try {
                        tempFile = File.createTempFile("buffer_overflow_test", ".tmp");
                        fos = new FileOutputStream(tempFile);
                        
                        // Write in chunks to avoid RAM overflow
                        byte[] buffer = new byte[1024 * 1024]; // 1MB chunks
                        for (int i = 0; i < buffer.length; i++) {
                            buffer[i] = (byte)(i % 256);
                        }
                        
                        for (int i = 0; i < sizeMB; i++) {
                            fos.write(buffer);
                            if (i % 10 == 0 && i > 0) {
                                logOutput("- " + i + " MB écrits...");
                            }
                        }
                        
                        logOutput("✓ Écriture réussie de " + sizeMB + " MB sur disque.");
                        logOutput("- Fichier temporaire: " + tempFile.getAbsolutePath());
                        logOutput("- Espace libre sur disque: " + tempFile.getFreeSpace() / (1024 * 1024) + " MB");
                        
                    } catch (IOException e) {
                        logOutput(" DÉPASSEMENT DÉTECTÉ! Impossible d'écrire " + sizeMB + " MB sur disque.");
                        logOutput("- Erreur: " + e.getMessage());
                        
                        if (tempFile != null) {
                            logOutput("- Espace libre sur disque: " + tempFile.getFreeSpace() / (1024 * 1024) + " MB");
                        }
                    } finally {
                        if (fos != null) {
                            try { fos.close(); } catch (IOException e) { /* ignore */ }
                        }
                        if (tempFile != null && tempFile.exists()) {
                            tempFile.delete();
                        }
                    }
                    break;
                    
                case "ByteBuffer":
                    logOutput("Tentative d'allocation d'un ByteBuffer de " + sizeMB + " MB...");
                    try {
                        // Try to allocate direct ByteBuffer (outside of the heap)
                        ByteBuffer buffer = ByteBuffer.allocateDirect((int)sizeBytes);
                        logOutput("✓ Allocation réussie d'un ByteBuffer de " + sizeMB + " MB.");
                        
                        // Write some data to ensure memory is allocated
                        for (int i = 0; i < Math.min(1000, buffer.capacity()); i++) {
                            buffer.put(i, (byte)(i % 256));
                        }
                        
                        // Some properties of the buffer
                        logOutput("- Capacité: " + buffer.capacity() + " bytes");
                        logOutput("- Direct: " + buffer.isDirect());
                        
                    } catch (OutOfMemoryError e) {
                        logOutput(" DÉPASSEMENT DÉTECTÉ! Impossible d'allouer un ByteBuffer de " + sizeMB + " MB.");
                        logOutput("- Erreur: " + e.getMessage());
                        
                        // Show available memory
                        Runtime runtime = Runtime.getRuntime();
                        long maxMemory = runtime.maxMemory() / (1024 * 1024);
                        long freeMemory = runtime.freeMemory() / (1024 * 1024);
                        logOutput("- Mémoire maximale JVM: " + maxMemory + " MB");
                        logOutput("- Mémoire libre JVM: " + freeMemory + " MB");
                    }
                    break;
            }
        } catch (Exception e) {
            logOutput(" Erreur lors du test: " + e.getMessage());
        }
    }

    private void showHeapInfo() {
        logOutput("\n--- Informations sur la mémoire de la JVM ---");
        Runtime runtime = Runtime.getRuntime();
        
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        logOutput("- Mémoire maximale: " + formatSize(maxMemory));
        logOutput("- Mémoire totale allouée: " + formatSize(totalMemory));
        logOutput("- Mémoire libre: " + formatSize(freeMemory));
        logOutput("- Mémoire utilisée: " + formatSize(usedMemory));
        
        // Memory visualization
        StringBuilder memBar = new StringBuilder("[");
        double usedPercentage = (double)usedMemory / totalMemory;
        int barLength = 50;
        int usedChars = (int)(usedPercentage * barLength);
        
        for (int i = 0; i < barLength; i++) {
            if (i < usedChars) {
                memBar.append("■");
            } else {
                memBar.append("□");
            }
        }
        memBar.append("] " + String.format("%.1f", usedPercentage * 100) + "%");
        
        logOutput(memBar.toString());
        logOutput("La JVM protège contre les dépassements de tampon mémoire,");
        logOutput("mais elle peut lancer OutOfMemoryError si la limite est atteinte.");
    }

    private String formatSize(long bytes) {
        final String[] units = {"bytes", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        
        while (size > 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }

    private void logOutput(String message) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(message + "\n");
            // Scroll to the bottom
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        try {
            // Set look and feel to system style
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            BufferOverflowDetector app = new BufferOverflowDetector();
            app.setVisible(true);
        });
    }
}