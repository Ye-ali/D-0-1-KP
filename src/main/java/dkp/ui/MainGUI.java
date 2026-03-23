package dkp.ui;

import dkp.algorithm.DKPResolver;
import dkp.io.DataReader;
import dkp.io.ResultSaver;
import dkp.model.DKPInstance;
import dkp.model.Solution;
import dkp.utils.ScatterPlot;
import dkp.utils.Sorter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 主界面类
 * 提供友好的图形用户界面
 */
public class MainGUI extends JFrame {
    
    private List<DKPInstance> instances;
    private DKPInstance currentInstance;
    private Solution currentSolution;
    
    private JTextArea outputArea;
    private JComboBox<String> instanceComboBox;
    private JComboBox<String> algorithmComboBox;
    private JLabel statusLabel;
    private JLabel fileLabel;
    
    public MainGUI() {
        setTitle("D{0-1}KP Problem Solver");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private void initComponents() {
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // 顶部面板 - 文件选择
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // 中间面板 - 控制按钮
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // 底部面板 - 输出和状态
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("数据文件"));
        
        fileLabel = new JLabel("未选择文件");
        fileLabel.setPreferredSize(new Dimension(400, 25));
        
        JButton browseButton = new JButton("浏览...");
        browseButton.addActionListener(e -> browseFile());
        
        panel.add(fileLabel);
        panel.add(browseButton);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // 控制面板
        JPanel controlPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("操作控制"));
        
        // 实例选择
        JPanel instancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        instancePanel.add(new JLabel("选择实例:"));
        instanceComboBox = new JComboBox<>();
        instanceComboBox.setPreferredSize(new Dimension(200, 25));
        instanceComboBox.setEnabled(false);
        instanceComboBox.addActionListener(e -> selectInstance());
        instancePanel.add(instanceComboBox);
        
        // 算法选择
        JPanel algoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        algoPanel.add(new JLabel("求解算法:"));
        algorithmComboBox = new JComboBox<>(new String[]{
            "动态规划",
            "贪心算法",
            "回溯算法"
        });
        algorithmComboBox.setPreferredSize(new Dimension(200, 25));
        algoPanel.add(algorithmComboBox);
        
        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton scatterButton = new JButton("显示散点图");
        scatterButton.addActionListener(e -> showScatterPlot());
        
        JButton sortButton = new JButton("按比率排序");
        sortButton.addActionListener(e -> sortByRatio());
        
        JButton solveButton = new JButton("求解");
        solveButton.addActionListener(e -> solve());
        
        JButton saveTxtButton = new JButton("保存为TXT");
        saveTxtButton.addActionListener(e -> saveAsTxt());
        
        JButton saveExcelButton = new JButton("保存为Excel");
        saveExcelButton.addActionListener(e -> saveAsExcel());
        
        buttonPanel.add(scatterButton);
        buttonPanel.add(sortButton);
        buttonPanel.add(solveButton);
        buttonPanel.add(saveTxtButton);
        buttonPanel.add(saveExcelButton);
        
        controlPanel.add(instancePanel);
        controlPanel.add(algoPanel);
        controlPanel.add(buttonPanel);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        // 输出区域
        outputArea = new JTextArea(15, 60);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("输出"));
        
        // 状态栏
        statusLabel = new JLabel("就绪");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void browseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            fileLabel.setText(selectedFile.getAbsolutePath());
            loadDataFile(selectedFile.getAbsolutePath());
        }
    }
    
    private void loadDataFile(String filePath) {
        try {
            instances = DataReader.readInstances(filePath);
            
            if (instances.isEmpty()) {
                appendOutput("错误：未能从文件中读取到有效数据！");
                appendOutput("请检查数据文件格式是否正确。");
                appendOutput("格式示例：");
                appendOutput("  容量=61500");
                appendOutput("  容积=100");
                appendOutput("  价值=408,921,1329,...");
                appendOutput("  重量=508,1021,1321,...");
                statusLabel.setText("数据读取失败");
                return;
            }
            
            instanceComboBox.removeAllItems();
            for (DKPInstance instance : instances) {
                instanceComboBox.addItem(instance.getName());
            }
            instanceComboBox.setEnabled(true);
            
            outputArea.setText("");
            appendOutput("成功加载 " + instances.size() + " 个实例。");
            appendOutput("");
            
            for (DKPInstance instance : instances) {
                appendOutput(instance.toString());
            }
            
            statusLabel.setText("已加载 " + instances.size() + " 个实例");
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "读取文件错误: " + e.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("文件读取失败");
        }
    }
    
    private void selectInstance() {
        String selectedName = (String) instanceComboBox.getSelectedItem();
        if (selectedName != null) {
            for (DKPInstance instance : instances) {
                if (instance.getName().equals(selectedName)) {
                    currentInstance = instance;
                    appendOutput("\n已选择实例: " + selectedName);
                    break;
                }
            }
        }
    }
    
    private void showScatterPlot() {
        if (currentInstance == null) {
            JOptionPane.showMessageDialog(this, 
                "请先选择一个实例！", 
                "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ScatterPlot.showChart(currentInstance);
        appendOutput("\n已显示 " + currentInstance.getName() + " 的散点图");
    }
    
    private void sortByRatio() {
        if (currentInstance == null) {
            JOptionPane.showMessageDialog(this, 
                "请先选择一个实例！", 
                "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String sortedInfo = Sorter.getSortedDetails(currentInstance);
        appendOutput("\n" + sortedInfo);
    }
    
    private void solve() {
        if (currentInstance == null) {
            JOptionPane.showMessageDialog(this, 
                "请先选择一个实例！", 
                "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String algorithm = (String) algorithmComboBox.getSelectedItem();
        appendOutput("\n正在求解 " + currentInstance.getName() + "，使用算法: " + algorithm + "...");
        
        switch (algorithm) {
            case "动态规划":
                currentSolution = DKPResolver.solveByDP(currentInstance);
                break;
            case "贪心算法":
                currentSolution = DKPResolver.solveByGreedy(currentInstance);
                break;
            case "回溯算法":
                currentSolution = DKPResolver.solveByBacktrack(currentInstance);
                break;
            default:
                currentSolution = DKPResolver.solve(currentInstance);
        }
        
        appendOutput(currentSolution.toString());
        statusLabel.setText("求解完成，耗时 " + currentSolution.getSolveTime() + " ms");
    }
    
    private void saveAsTxt() {
        if (currentSolution == null) {
            JOptionPane.showMessageDialog(this, 
                "请先求解问题！", 
                "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        fileChooser.setSelectedFile(new File(currentSolution.getInstance().getName() + "_solution.txt"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                ResultSaver.saveAsTxt(currentSolution, selectedFile.getAbsolutePath());
                appendOutput("\n结果已保存到: " + selectedFile.getAbsolutePath());
                statusLabel.setText("结果已保存为TXT");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "保存文件错误: " + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveAsExcel() {
        if (currentSolution == null) {
            JOptionPane.showMessageDialog(this, 
                "请先求解问题！", 
                "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));
        fileChooser.setSelectedFile(new File(currentSolution.getInstance().getName() + "_solution.xlsx"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                ResultSaver.saveAsExcel(currentSolution, selectedFile.getAbsolutePath());
                appendOutput("\n结果已保存到: " + selectedFile.getAbsolutePath());
                statusLabel.setText("结果已保存为Excel");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "保存文件错误: " + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void appendOutput(String text) {
        outputArea.append(text + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
    
    /**
     * 启动GUI
     */
    public static void start() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        });
    }
}
