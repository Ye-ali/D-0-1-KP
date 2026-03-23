package dkp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 求解结果类
 * 包含最优解的详细信息
 */
public class Solution {
    private DKPInstance instance;       // 问题实例
    private List<Item> selectedItems;   // 选中的物品列表
    private int totalProfit;            // 总价值
    private int totalWeight;            // 总重量
    private long solveTime;             // 求解时间(毫秒)
    private String algorithm;           // 使用的算法
    
    public Solution(DKPInstance instance) {
        this.instance = instance;
        this.selectedItems = new ArrayList<>();
        this.totalProfit = 0;
        this.totalWeight = 0;
        this.solveTime = 0;
        this.algorithm = "";
    }
    
    public void addItem(Item item) {
        selectedItems.add(item);
        totalProfit += item.getProfit();
        totalWeight += item.getWeight();
    }
    
    public void setSolveTime(long solveTime) {
        this.solveTime = solveTime;
    }
    
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    
    public DKPInstance getInstance() {
        return instance;
    }
    
    public List<Item> getSelectedItems() {
        return selectedItems;
    }
    
    public int getTotalProfit() {
        return totalProfit;
    }
    
    public int getTotalWeight() {
        return totalWeight;
    }
    
    public long getSolveTime() {
        return solveTime;
    }
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    /**
     * 检查解是否有效(是否满足约束条件)
     */
    public boolean isValid() {
        // 检查是否从每个项集中至多选择了一个物品
        boolean[] setSelected = new boolean[instance.getSetCount()];
        for (Item item : selectedItems) {
            int setIndex = item.getSetIndex();
            if (setSelected[setIndex]) {
                return false; // 同一个项集中选择了多个物品
            }
            setSelected[setIndex] = true;
        }
        
        // 检查是否超过背包容量
        return totalWeight <= instance.getCapacity();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== Solution ==========\n");
        sb.append("Instance: ").append(instance.getName()).append("\n");
        sb.append("Algorithm: ").append(algorithm).append("\n");
        sb.append("Total Profit: ").append(totalProfit).append("\n");
        sb.append("Total Weight: ").append(totalWeight).append("\n");
        sb.append("Capacity: ").append(instance.getCapacity()).append("\n");
        sb.append("Solve Time: ").append(solveTime).append(" ms\n");
        sb.append("Selected Items(").append(selectedItems.size()).append("):\n");
        
        for (Item item : selectedItems) {
            sb.append("  ").append(item.toString()).append("\n");
        }
        sb.append("==============================\n");
        return sb.toString();
    }
}
