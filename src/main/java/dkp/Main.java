package dkp;

import dkp.algorithm.DKPResolver;
import dkp.io.DataReader;
import dkp.io.ResultSaver;
import dkp.model.DKPInstance;
import dkp.model.Solution;
import dkp.ui.MainGUI;
import dkp.utils.ScatterPlot;
import dkp.utils.Sorter;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * D{0-1}KP问题求解程序主入口
 * 支持命令行模式和图形界面模式
 */
public class Main {
    
    private static final String VERSION = "1.0";
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    D{0-1}KP Problem Solver v" + VERSION);
        System.out.println("========================================");
        System.out.println();
        
        if (args.length == 0) {
            // 无参数，显示启动菜单
            showStartupMenu();
        } else {
            // 有参数，启动命令行模式
            runCommandLineMode(args[0]);
        }
    }
    
    /**
     * 显示启动菜单
     */
    private static void showStartupMenu() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("请选择运行模式:");
        System.out.println("1. 图形界面模式 (GUI)");
        System.out.println("2. 命令行模式");
        System.out.println("0. 退出");
        System.out.println();
        System.out.print("请输入选项 (0-2): ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                System.out.println("\n启动图形界面模式...");
                MainGUI.start();
                break;
            case "2":
                System.out.print("\n请输入数据文件路径: ");
                String filePath = scanner.nextLine().trim();
                runCommandLineMode(filePath);
                break;
            case "0":
                System.out.println("再见!");
                System.exit(0);
                break;
            default:
                System.out.println("无效选项，启动图形界面模式...");
                MainGUI.start();
        }
    }
    
    /**
     * 命令行模式
     */
    private static void runCommandLineMode(String filePath) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n命令行模式");
        System.out.println();
        
        // 读取数据
        List<DKPInstance> instances;
        try {
            instances = DataReader.readInstances(filePath);
            if (instances.isEmpty()) {
                System.err.println("错误：未能从文件中读取到有效数据！");
                System.err.println("请检查数据文件格式是否正确。");
                System.err.println("格式示例：");
                System.err.println("  容量=61500");
                System.err.println("  容积=100");
                System.err.println("  价值=408,921,1329,...");
                System.err.println("  重量=508,1021,1321,...");
                return;
            }
            System.out.println("成功加载 " + instances.size() + " 个实例。");
            System.out.println();
        } catch (IOException e) {
            System.err.println("读取文件错误: " + e.getMessage());
            return;
        }
        
        // 显示可用实例
        System.out.println("可用实例:");
        for (int i = 0; i < instances.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + instances.get(i).getName());
        }
        System.out.println();
        
        // 选择实例
        System.out.print("选择实例 (1-" + instances.size() + "): ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > instances.size()) {
                System.err.println("无效选择！");
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("输入错误！");
            return;
        }
        
        DKPInstance selectedInstance = instances.get(choice - 1);
        System.out.println();
        System.out.println(selectedInstance.toString());
        
        // 主菜单
        boolean running = true;
        Solution currentSolution = null;
        
        while (running) {
            System.out.println();
            System.out.println("========== 主菜单 ==========");
            System.out.println("1. 显示实例信息");
            System.out.println("2. 显示散点图");
            System.out.println("3. 按第三项价值重量比排序");
            System.out.println("4. 使用动态规划求解");
            System.out.println("5. 使用贪心算法求解");
            System.out.println("6. 使用回溯算法求解");
            System.out.println("7. 保存结果为TXT文件");
            System.out.println("8. 保存结果为Excel文件");
            System.out.println("0. 退出");
            System.out.println("===========================");
            System.out.print("请输入选项: ");
            
            String menuChoice = scanner.nextLine().trim();
            System.out.println();
            
            switch (menuChoice) {
                case "1":
                    showInstanceInfo(selectedInstance);
                    break;
                    
                case "2":
                    System.out.println("正在生成散点图...");
                    ScatterPlot.showChart(selectedInstance);
                    break;
                    
                case "3":
                    Sorter.printSorted(selectedInstance);
                    break;
                    
                case "4":
                    System.out.println("正在使用动态规划求解...");
                    currentSolution = DKPResolver.solveByDP(selectedInstance);
                    System.out.println(currentSolution.toString());
                    break;
                    
                case "5":
                    System.out.println("正在使用贪心算法求解...");
                    currentSolution = DKPResolver.solveByGreedy(selectedInstance);
                    System.out.println(currentSolution.toString());
                    break;
                    
                case "6":
                    System.out.println("正在使用回溯算法求解...");
                    currentSolution = DKPResolver.solveByBacktrack(selectedInstance);
                    System.out.println(currentSolution.toString());
                    break;
                    
                case "7":
                    if (currentSolution == null) {
                        System.out.println("请先求解问题！");
                    } else {
                        String txtPath = selectedInstance.getName() + "_solution.txt";
                        try {
                            ResultSaver.saveAsTxt(currentSolution, txtPath);
                            System.out.println("结果已保存到: " + txtPath);
                        } catch (IOException e) {
                            System.err.println("保存文件错误: " + e.getMessage());
                        }
                    }
                    break;
                    
                case "8":
                    if (currentSolution == null) {
                        System.out.println("请先求解问题！");
                    } else {
                        String excelPath = selectedInstance.getName() + "_solution.xlsx";
                        try {
                            ResultSaver.saveAsExcel(currentSolution, excelPath);
                            System.out.println("结果已保存到: " + excelPath);
                        } catch (IOException e) {
                            System.err.println("保存文件错误: " + e.getMessage());
                        }
                    }
                    break;
                    
                case "0":
                    running = false;
                    System.out.println("再见!");
                    break;
                    
                default:
                    System.out.println("无效选项！");
            }
        }
        
        scanner.close();
    }
    
    /**
     * 显示实例详细信息
     */
    private static void showInstanceInfo(DKPInstance instance) {
        System.out.println(instance.toString());
        System.out.println("数据验证: " + (instance.validate() ? "通过" : "失败"));
        System.out.println();
        System.out.println("前5个项集:");
        for (int i = 0; i < Math.min(5, instance.getSetCount()); i++) {
            System.out.println(instance.getItemSet(i).toString());
        }
        if (instance.getSetCount() > 5) {
            System.out.println("... (还有 " + (instance.getSetCount() - 5) + " 个项集)");
        }
    }
}
