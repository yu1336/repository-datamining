package org.project.modules.classifier.decisiontree.builder;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.project.modules.classifier.decisiontree.data.BestAttribute;
import org.project.modules.classifier.decisiontree.data.Data;
import org.project.modules.classifier.decisiontree.data.Instance;
import org.project.modules.classifier.decisiontree.node.TreeNode;

public abstract class DecisionTreeAbstractBuilder extends BuilderAbstractImpl {

	@Override
	public Object build(Data data) {
		//获取数据集的分类分割集合
		Map<Object, List<Instance>> splits = data.getSplits();
		//如果只有一个样本，将该样本所属分类作为新样本的分类
		if (splits.size() == 1) {
			return splits.keySet().iterator().next();
		}
		String[] attributes = data.getAttributes();
		// 如果没有供决策的属性，则将样本集中具有最多样本的分类作为新样本的分类，即投票选举出最多个数分类
		if (attributes.length == 0) {
			return obtainMaxCategory(splits);
		}
		// 选取最优最佳属性信息,交由子类去实现各个算法
		BestAttribute bestAttribute = chooseBestAttribute(data);
		// 决策树根结点，分支属性为选取的分割属性
		int bestAttrIndex = bestAttribute.getIndex();
		if (bestAttrIndex == -1) {
			return obtainMaxCategory(splits);
		}
		TreeNode treeNode = new TreeNode(attributes[bestAttrIndex]);
		// 已用过的测试属性不应再次被选为分割属性
		String[] subAttributes = new String[attributes.length - 1];
		for (int i = 0, j = 0; i < attributes.length; i++) {
			if (i != bestAttrIndex) {
				subAttributes[j++] = attributes[i];
			}
		}
		// 根据分支属性生成分支分裂信息
		Map<Object, Map<Object, List<Instance>>> subSplits = bestAttribute.getSplits();
		for (Entry<Object, Map<Object, List<Instance>>> entry : subSplits.entrySet()) {
			Object attrValue = entry.getKey();
			Data subData = new Data(subAttributes, entry.getValue());
			Object child = build(subData);
			treeNode.setChild(attrValue, child);
		}
		return treeNode;
	}
	
	/**
	 * 获取数据集的最佳属性信息
	 * @param data
	 * @return
	 */
	public abstract BestAttribute chooseBestAttribute(Data data);
	

}
