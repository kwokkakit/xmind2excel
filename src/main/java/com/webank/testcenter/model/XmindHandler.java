package com.webank.testcenter.model;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.xmind.core.*;

import java.io.IOException;
import java.util.List;

/**
 * @Package: com.webank.testcenter.model
 * @Author: huahuaxu
 * @CreateDate: 2018/5/29
 * @Description:
 * @History:
 */
public class XmindHandler {
    private String xmindFile;

    public XmindHandler(String xmindFile){
        this.xmindFile = xmindFile;
    }

    public String getXmindFile(){
        return this.xmindFile;
    }

    /**
     * @Description: 解析根节点，并以此为入口进入递归解析所有子节点
     * @Param ExcelHandler: 案例结果excel的处理对象
     */
    public void xmind2Excel(ExcelHandler resultExcelHandler){
        IWorkbookBuilder workbookBuilder = Core.getWorkbookBuilder();
        IWorkbook workbook = null;
        try {
            workbook = workbookBuilder.loadFromPath(this.xmindFile);
        } catch (CoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ISheet sheet = workbook.getPrimarySheet();
        //获取根节点
        ITopic rootTopic = sheet.getRootTopic();
        //获取根主题的文字内容
        String rootTopicText = rootTopic.getTitleText();
        System.out.println("根主题："+rootTopicText);
        /**
        try {
            //获取根主题备注内容
            INotes notes = rootTopic.getNotes();
            IPlainNotesContent newplainContent=(IPlainNotesContent)notes.getContent(INotes.PLAIN);
            System.out.println(newplainContent.getTextContent());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
         */
        //获取根主题的子主题
        List<ITopic> itopics = rootTopic.getAllChildren();
        //遍历根主题的子主题
        for(ITopic childtopic:itopics){
            //System.out.println(childtopic.getTitleText());
            //String topicText = childtopic.getTitleText();
            topicRecursion(resultExcelHandler,childtopic,rootTopicText);
        }
    }

    /**
     * @Description: 遍历子主题，获取每条案例的用例目录、用例名称、前置条件、执行步骤、预期结果
     * @param rootTopic:根主题
     * @param casePath:用例目录
     */
    public  void topicRecursion(ExcelHandler resultExcelHandler, ITopic rootTopic, String casePath) throws NullPointerException {
        List<ITopic> childTopics = rootTopic.getAllChildren();
        //如果子主题大于2，证明是场景分类，需要再次进入递归
        if (childTopics.size()>1){
            //更新用例目录
            String updatePath = casePath +"-"+rootTopic.getTitleText();
            for (ITopic childtopic: childTopics){
                topicRecursion(resultExcelHandler,childtopic, updatePath);
            }
        }
        else {
            //如果子主题个数不大于2，有两种可能，第一种是该业务场景分类只有1类，所以用try去尝试
            try {
                rootTopic.getAllChildren().get(0).getAllChildren().get(0).getAllChildren().get(0);
                String updatePath = casePath +"-"+rootTopic.getTitleText();
                topicRecursion(resultExcelHandler,rootTopic.getAllChildren().get(0),updatePath);
            }
            //子主题个数不大于2，还有一种可能是该子主题已经是案例名称级别
            catch (IndexOutOfBoundsException e) {
                String caseName = rootTopic.getTitleText();                         //案例名称
                //获取案例步骤
                String caseStep = "";
                try {
                    INotes notes = rootTopic.getNotes();
                    IPlainNotesContent newplainContent=(IPlainNotesContent)notes.getContent(INotes.PLAIN);
                    caseStep = newplainContent.getTextContent();
                }catch (NullPointerException exception){
                    e.printStackTrace();
                }
                List<ITopic> caseConTopics = rootTopic.getAllChildren();
                String caseCondition = caseConTopics.get(0).getTitleText();         //前置条件
                List<ITopic> caseResultTopics = caseConTopics.get(0).getAllChildren();
                String caseResult = caseResultTopics.get(0).getTitleText();         //预期结果
                System.out.println("案例路径:" + casePath);                         //用例目录
                System.out.println("案例名称:" + caseName);
                System.out.println("前置条件:" + caseCondition);
                System.out.println("执行步骤:" + caseStep);
                System.out.println("预期结果:" + caseResult);
                try {
                    resultExcelHandler.writeExcel(casePath,caseName,caseCondition,caseStep,caseResult);
                } catch (IOException exception) {
                    exception.printStackTrace();
                } catch (InvalidFormatException exception) {
                    exception.printStackTrace();
                }
                return;
            }
        }
    }
}
