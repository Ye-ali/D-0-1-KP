package dkp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * D{0-1}KP问题实例类
 * 包含问题名称、背包容量、项集列表等信息
 */
public class DKPInstance {
    private String name;                // 实例名称(如IDKP1, IDKP2等)
    private int dimension;              // 维度(3*n)
    private int capacity;               // 背包容量
    private List<ItemSet> itemSets;     // 项集列表
    private List<Item> allItems;        // 所有物品的列表
    
    public DKPInstance(String name, int dimension, int capacity) {
        this.name = name;
        this.dimension = dimension;
        this.capacity = capacity;
        this.itemSets = new ArrayList<>();
        this.allItems = new ArrayList<>();
    }
    
    public void addItemSet(ItemSet itemSet) {
        itemSets.add(itemSet);
        allItems.addAll(itemSet.getItems());
    }
    
    public String getName() {
        return name;
    }
    
    public int getDimension() {
        return dimension;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public List<ItemSet> getItemSets() {
        return itemSets;
    }
    
    public List<Item> getAllItems() {
        return allItems;
    }
    
    /**
     * 获取项集数量
     */
    public int getSetCount() {
        return itemSets.size();
    }
    
    /**
     * 获取指定索引的项集
     */
    public ItemSet getItemSet(int index) {
        if (index >= 0 && index < itemSets.size()) {
            return itemSets.get(index);
        }
        return null;
    }
    
    /**
     * 验证所有项集是否满足约束条件
     */
    public boolean validate() {
        for (ItemSet set : itemSets) {
            if (!set.validate()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("D{0-1}KP Instance: ").append(name).append("\n");
        sb.append("Dimension: ").append(dimension).append("\n");
        sb.append("Capacity: ").append(capacity).append("\n");
        sb.append("Number of ItemSets: ").append(itemSets.size()).append("\n");
        sb.append("========================================\n");
        return sb.toString();
    }
}
