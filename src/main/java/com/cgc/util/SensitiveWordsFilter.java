package com.cgc.util;

import org.apache.commons.lang3.CharUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveWordsFilter {

    //只维护一个根节点，以后如果有需要在添加
    private TrieNode rootNode = new TrieNode();

    @PostConstruct  //表示在框架使用无参构造创建了该类的对象之后，调用这个方法为属性rootNode初始化
    //根据敏感词文件构造前缀树
    public void constructTrie() {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String keywords;
            while ((keywords = reader.readLine()) != null) {
                this.addKeywords(keywords);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //向前缀树添加敏感词的方法(写这种东西必须要用纸）
    public void addKeywords(String keyword) {
        TrieNode curRootNode = rootNode;
        for (char c : keyword.toCharArray()) {
            TrieNode subNode = curRootNode.getSubNode(c);
            if (subNode == null) {
                subNode = new TrieNode();
                curRootNode.addSubNode(c, subNode);
            }
            curRootNode = subNode;
        }
        if (curRootNode != rootNode) {
            curRootNode.setKeyWordEnd(true);
        }
    }
    //判断是否是符号
    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //实现使用构建好的前缀树，从传入的文本中搜索是否有敏感词(这玩意真他娘的难写，结合图片多写两遍吧，也没别的方法）
    public String filter(String text) {
        if (text.isEmpty()) {
            return null;
        }
        //三个指针
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;
        //存储过滤结果
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);
            if (isSymbol(c)) {
                position++;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;
            } else if (tempNode.isKeyWordEnd) {
                sb.append("*");
                begin = ++position;
                tempNode = rootNode;
            } else {
                position++;
            }
        }
        return sb.toString();
    }

    //定义前缀树数据结构
    private class TrieNode {
        //是否是关键词结束标识
        private boolean isKeyWordEnd = false;
        //子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        //添加子节点
        public void addSubNode(Character c, TrieNode trieNode) {
            subNodes.put(c, trieNode);
        }

        //获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

        public boolean getIsKeyWordEnd() {
            return this.isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            this.isKeyWordEnd = keyWordEnd;
        }

        @Override
        public String toString() {
            return "TrieNode{" +
                    "isKeyWordEnd=" + isKeyWordEnd +
                    ", subNodes=" + subNodes.toString() +
                    '}';
        }
    }

}
