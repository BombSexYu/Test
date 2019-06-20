package SensitiveWords;

import java.util.HashMap;
import java.util.Map;

/**
 * @创建用户: 阿宇
 * @创建时间: 2019/4/19 13:42
 * @类描述: 树的每个节点
 */
public class TreeNode {
    /**
     * true 关键词的终结 ； false 继续
     */
    private boolean end = false;

    /**
     * key下一个字符，value是对应的节点
     */
    private Map<Character, TreeNode> subNodes = new HashMap<>();

    /**
     * 向指定位置添加节点树
     */
    void addSubNode(Character key, TreeNode node) {
        subNodes.put(key, node);
    }

    /**
     * 获取下个节点
     */
    TreeNode getSubNode(Character key) {
        return subNodes.get(key);
    }

    boolean isKeywordEnd() {
        return end;
    }

    void setKeywordEnd(boolean end) {
        this.end = end;
    }

    public int getSubNodeCount() {
        return subNodes.size();
    }
}
