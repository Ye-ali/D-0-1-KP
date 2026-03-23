package dkp.io;

import dkp.model.DKPInstance;
import dkp.model.Item;
import dkp.model.ItemSet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据读取类
 * 支持格式：
 * 尺寸=3*100
 * 容积=61500
 * 价值=408,921,1329,...
 * 重量=508,1021,1321,...
 */
public class DataReader {
    
    /**
     * 从文件中读取D{0-1}KP实例
     * @param filePath 数据文件路径
     * @return DKPInstance列表
     */
    public static List<DKPInstance> readInstances(String filePath) throws IOException {
        List<DKPInstance> instances = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String instanceName = "DKP_Instance";
            int capacity = 0;
            int setCount = 0;
            List<Integer> profits = null;
            List<Integer> weights = null;
            int lineNum = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                
                // 跳过空行和注释行
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }
                
                // 检测实例开始（以冒号结尾的行）
                if (line.endsWith(":") && !line.contains("=")) {
                    // 保存前一个实例
                    if (profits != null && weights != null && !profits.isEmpty() && !weights.isEmpty()) {
                        DKPInstance instance = createInstance(instanceName, capacity, setCount, profits, weights);
                        if (instance != null) {
                            instances.add(instance);
                        }
                    }
                    // 开始新实例
                    instanceName = line.substring(0, line.length() - 1);
                    capacity = 0;
                    setCount = 0;
                    profits = null;
                    weights = null;
                    continue;
                }
                
                // 解析键值对
                if (line.contains("=")) {
                    int eqIndex = line.indexOf('=');
                    String key = line.substring(0, eqIndex).trim();
                    String value = line.substring(eqIndex + 1).trim();
                    
                    switch (key) {
                        case "尺寸":
                        case "DIMENSION":
                        case "D":
                        case "d":
                            // 尺寸格式：3*100
                            setCount = parseDimension(value, lineNum);
                            break;
                        case "容积":
                        case "容量":
                        case "CAPACITY":
                        case "C":
                        case "c":
                            capacity = parseInt(value.replaceAll("[^0-9]", ""), lineNum);
                            break;
                        case "价值":
                        case "利润":
                        case "PROFIT":
                        case "PROFITS":
                        case "P":
                        case "p":
                            profits = parseList(value);
                            break;
                        case "重量":
                        case "权重":
                        case "WEIGHT":
                        case "WEIGHTS":
                        case "W":
                        case "w":
                            weights = parseList(value);
                            break;
                        case "名称":
                        case "NAME":
                            instanceName = value;
                            break;
                    }
                }
            }
            
            // 保存最后一个实例
            if (profits != null && weights != null && !profits.isEmpty() && !weights.isEmpty()) {
                DKPInstance instance = createInstance(instanceName, capacity, setCount, profits, weights);
                if (instance != null) {
                    instances.add(instance);
                }
            }
        }
        
        return instances;
    }
    
    /**
     * 解析尺寸（如 3*100）
     * 返回项集数量
     */
    private static int parseDimension(String value, int lineNum) {
        try {
            value = value.replaceAll("[^0-9*]", "");
            if (value.contains("*")) {
                String[] parts = value.split("\\*");
                // 3*100 表示 3个物品/项集 * 100个项集
                // 返回项集数量
                if (parts.length >= 2) {
                    return Integer.parseInt(parts[1].trim());
                }
            } else {
                // 直接是项集数量
                return Integer.parseInt(value.trim()) / 3;
            }
        } catch (Exception e) {
            System.err.println("第 " + lineNum + " 行错误：无法解析尺寸 '" + value + "'");
        }
        return 0;
    }
    
    /**
     * 解析整数
     */
    private static int parseInt(String value, int lineNum) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            System.err.println("第 " + lineNum + " 行错误：无法解析整数 '" + value + "'");
            return 0;
        }
    }
    
    /**
     * 解析数值列表
     */
    private static List<Integer> parseList(String line) {
        List<Integer> values = new ArrayList<>();
        String[] parts = line.split(",");
        for (String part : parts) {
            part = part.trim();
            if (!part.isEmpty()) {
                try {
                    values.add(Integer.parseInt(part));
                } catch (NumberFormatException e) {
                    // 忽略无法解析的值
                }
            }
        }
        return values;
    }
    
    /**
     * 创建实例
     */
    private static DKPInstance createInstance(String name, int capacity, int setCount,
                                               List<Integer> profits, List<Integer> weights) {
        if (profits.size() != weights.size()) {
            System.err.println("错误：价值数量(" + profits.size() + ")与重量数量(" + weights.size() + ")不匹配！");
            return null;
        }
        
        if (profits.size() % 3 != 0) {
            System.err.println("错误：物品数量(" + profits.size() + ")必须是3的倍数！");
            return null;
        }
        
        int actualSetCount = profits.size() / 3;
        
        // 如果指定了项集数，验证是否匹配
        if (setCount > 0 && setCount != actualSetCount) {
            System.out.println("注意：尺寸指定的项集数(" + setCount + ")与实际项集数(" + actualSetCount + ")不一致，使用实际值");
        }
        
        // 如果容量为0，自动计算
        if (capacity == 0) {
            int totalWeight = 0;
            for (int w : weights) {
                totalWeight += w;
            }
            capacity = totalWeight / 2;
            System.out.println("容积未指定，自动设置为：" + capacity);
        }
        
        int dimension = profits.size();
        DKPInstance instance = new DKPInstance(name, dimension, capacity);
        
        // 填充数据
        int itemId = 0;
        for (int i = 0; i < actualSetCount; i++) {
            ItemSet itemSet = new ItemSet(i);
            
            for (int j = 0; j < 3; j++) {
                int index = i * 3 + j;
                Item item = new Item(
                    itemId++,
                    profits.get(index),
                    weights.get(index),
                    i,
                    j
                );
                itemSet.addItem(item);
            }
            
            instance.addItemSet(itemSet);
        }
        
        return instance;
    }
    
    /**
     * 读取单个实例（兼容旧接口）
     */
    public static DKPInstance readSingleInstance(String filePath, String instanceName) 
            throws IOException {
        List<DKPInstance> instances = readInstances(filePath);
        
        for (DKPInstance instance : instances) {
            if (instance.getName().equals(instanceName)) {
                return instance;
            }
        }
        
        return instances.isEmpty() ? null : instances.get(0);
    }
}
