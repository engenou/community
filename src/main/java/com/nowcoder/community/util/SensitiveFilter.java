package com.nowcoder.community.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eugen
 * @creat 2022-05-05 16:40
 */
@Slf4j
@Component
public class SensitiveFilter {

    // 替换符号
    private static final String REPLACEMENT = "***";

    private TrieNode rootNode = new TrieNode();

    /**
     *  初始化:调用此类的构造器后
     *
     */
    @PostConstruct
    public void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;
            while((keyword = reader.readLine()) != null){
                this.addKeyWord(keyword);
            }
        }catch (IOException e){
            log.error("加载敏感词文件失败；" + e.getMessage());
        }
    }


    /**
     *  将敏感词添加到前缀树
     */
    private void addKeyWord(String keyword){
        TrieNode tempNode = rootNode;
        for(int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            // 若子节点没有，初始化子节点；若有子节点，直接用
            if(subNode == null){
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            // 根节点指向子节点,进入下一个循环
            tempNode = subNode;

            // 设置结束标志
            if(i == keyword.length() - 1){
                tempNode.setIsEnd(true);
            }
        }
    }

    /**
     *  ==========  过滤敏感词  =============
     * @param text 过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)) return null;
        // 指针1
        TrieNode tempNode = rootNode;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        StringBuilder sb = new StringBuilder();

        while(position < text.length()){
            char c = text.charAt(position);
            // 跳过字符
            if(isSymbol(c)){
                // 若指针1在根节点，将此符号计入结果，让指针2下一步
                if(tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                // 无论符号在哪，指针3都下一步
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                // 以begin开头的字符不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 1重新指向根节点
                tempNode = rootNode;
            }else if(tempNode.getIsEnd()){
                // 发现敏感词
                sb.append(REPLACEMENT);
                // 进入下一个位置
                begin = ++position;
                tempNode = rootNode;
            }else {
                // 检查下一个字母
                position++;
            }
        }

        // 将最后一批字符加入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c){
        // 0x2E80 --- 0x9FFF 是东亚文字
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    /**
     * 前缀树
     */
    private class TrieNode{
        //关键词结束标志
        private boolean isEnd = false;

        // 子节点（key：子节点字符，value:子节点）
        private Map<Character,TrieNode> subNode = new HashMap<>();

        public boolean getIsEnd() {
            return isEnd;
        }

        public void setIsEnd(boolean end) {
            isEnd = end;
        }

        // 添加子节点
        public void addSubNode(Character c, TrieNode node){
            subNode.put(c,node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c){
            return subNode.get(c);
        }


    }
}
