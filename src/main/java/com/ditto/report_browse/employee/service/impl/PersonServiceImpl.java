package com.ditto.report_browse.employee.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;

import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.ditto.report_browse.employee.entity.Person;
import com.ditto.report_browse.employee.mapper.PersonMapper;
import com.ditto.report_browse.employee.service.PersonService;
import com.ditto.report_browse.tex_component.tex_console.entity.TexTemplate;
import com.ditto.report_browse.tex_component.tex_console.entity.TexTemplateCell;
import com.ditto.report_browse.tex_component.tex_console.mapper.TexTemplateCellMapper;
import com.ditto.report_browse.tex_component.tex_console.mapper.TexTemplateMapper;
import com.ditto.report_browse.tex_component.tex_console.service.impl.TexTemplateCellServiceImpl;
import com.ditto.report_browse.tex_component.tex_export.GoExport;
import com.ditto.report_browse.tex_component.tex_export.IndexSetCellData;
import com.ditto.report_browse.tex_component.tex_export.SxssfExportOrdinaryContext;
import com.ditto.report_browse.tex_component.tex_util.*;

import com.ditto.report_browse.tex_component.tex_util.request.ExportFileResponseUtil;
import com.ditto.report_browse.tex_component.tex_util.template_stream.TexInputStreamOperate;
import com.ditto.report_browse.tex_component.tex_util.template_stream.TexOssTemplateStream;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PersonServiceImpl implements PersonService {
    @Autowired
    PersonMapper personMapper;
    @Autowired
    TexTemplateMapper exTemplateMapper;
    @Autowired
    TexTemplateCellServiceImpl texTemplateCellServiceImpl;
    @Autowired
    SxssfExportOrdinaryContext sxssfExportOrdinaryContext; @Autowired
    private TexOssTemplateStream texOssTemplateStream;



   //easy
    public void HMC_XZ_E(HttpServletResponse response) {
        TexThreadLocal.setExTemplate(exTemplateMapper.getExTemplate("HMC_XZ"));
        ExportFileResponseUtil responseUtil = new ExportFileResponseUtil(response, TexThreadLocal.getExTemplate().getFileName(), "xlsx");


        long startTime = System.currentTimeMillis();
        texOssTemplateStream.downloadInput(TexThreadLocal.getExTemplate().getTemplateUrl(), new TexInputStreamOperate() {
            @Override
            public void closeBefore(InputStream inputStream) throws Exception {

                //FillConfig build = FillConfig.builder().forceNewRow(false).build();


                ThirdRowStyleWriteHandler styleHandler = new ThirdRowStyleWriteHandler();

                ExcelWriter excelWriter = EasyExcel.write(responseUtil.getOutputStream()).registerWriteHandler(styleHandler)
                        .withTemplate(inputStream).inMemory(true).build();
                // 在 write 方法之后， 在 sheet方法之前都是设置WriteWorkbook的参数
                WriteSheet writeSheet = EasyExcelFactory.writerSheet().build();

                //核心：用TTL包装线程池（一行代码，仅此而已）
                ExecutorService executorService = TtlExecutors.getTtlExecutorService( Executors.newFixedThreadPool(10));
                CountDownLatch countDownLatch = new CountDownLatch(10);
                executorService.execute(()->{
                    for(int i=0;i<10;i++){
                        int pageNum = i+1;
                        int pageSize = 10000;
                        List<Map<String, Object>> maps = personMapper.HMC_XZ(new Page<>(pageNum, pageSize, false));
                    synchronized (writeSheet){

                        TexFormulaUtil.cellFormulaBatch(TexThreadLocal.getExFormulas(),maps);
                        excelWriter.fill(maps, writeSheet);
                    }
                    countDownLatch.countDown();
                    }
                });
                try {
                    countDownLatch.await();
                    executorService.shutdown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Workbook workbook = excelWriter.writeContext().writeWorkbookHolder().getWorkbook();
                workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
                excelWriter.finish();
            }
            @Override
            public void closeAfter() {}
        });
        // 执行代码块
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("程序执行耗时：" + timeElapsed + "毫秒");
    }

    public void HMC_XZ2(HttpServletResponse response){
        TexThreadLocal.setExTemplate(exTemplateMapper.getExTemplate("HMC_XZ"));
        List<IndexSetCellData> otherData = new ArrayList<>();
        otherData.add(new IndexSetCellData(1,0,"数据1",true));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
        otherData.add(new IndexSetCellData(1,13,"数据日期:"+ LocalDate.now().format(timeFormatter),true));

        GoExport goExport =(export)->{
            export.copyHeed(true).dataList(personMapper.HMC_XZ1()).otherCellData(otherData).write();
            export.copyHeed(1,2).
                    copyHeed(true).
                    dataList(personMapper.HMC_XZ2())
                    .otherCellData(new IndexSetCellData(0,0,"数据2",true)).write();
            //export.dataList(personMapper.HMC_XZ1()).write();
        };
        sxssfExportOrdinaryContext.sxssfExportOrdinary().export(response, goExport);
    }


 /*   public void HMC_XZ0(HttpServletResponse response){
        long l = System.currentTimeMillis();
        TexThreadLocal.setExTemplate(exTemplateMapper.getExTemplate("HMC_XZ"));
        List<IndexSetCellData> otherData = new ArrayList<>();
        otherData.add(new IndexSetCellData(1,0,"数据1"));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
        otherData.add(new IndexSetCellData(1,13,"数据日期:"+ LocalDate.now().format(timeFormatter)));

        GoExport goExport =(export)->{
            export.copyHeed(true).dataList(personMapper.HMC_XZ0()).otherCellData(otherData).write();
        };
        sxssfExportOrdinaryContext.sxssfExportOrdinary().export(response, goExport);
        System.out.println("用时"+(System.currentTimeMillis()-l)+"毫秒");
    }
*/
    //单位分页
    public void HMC_XZ_S(HttpServletResponse response){
        long l = System.currentTimeMillis();
        TexTemplate texTemplate = exTemplateMapper.getExTemplate("HMC_XZ_S");
        TexThreadLocal.setExTemplate(texTemplate);

        //核心：用TTL包装线程池（一行代码，仅此而已）
        //ExecutorService executorService = TexExecutorCpuMonitor.getMonitoredTtlExecutorService(Executors.newFixedThreadPool(10));
        ExecutorService executorService = TtlExecutors.getTtlExecutorService( Executors.newFixedThreadPool(3));
        CountDownLatch countDownLatch = new CountDownLatch(3);
        GoExport goExport =(export)->{
            export.getWorkbook().setSheetHidden(0, true);
            for(int i=0;i<3;i++){
                int pageNum = i+1;
                int pageSize = 10000;
                executorService.execute(()->{
                try {
                    List<Map<String, Object>> maps = personMapper.HMC_XZ(new Page<>(pageNum, pageSize, false));
                    synchronized (export){
                        List<IndexSetCellData> otherData = new ArrayList<>();
                        otherData.add(new IndexSetCellData(1,0,"单位"+pageNum,true));
                        DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.CHINA);;
                        otherData.add(new IndexSetCellData(1,13,"数据日期:"+ LocalDate.now().format(timeFormatter),true));
                        String sheetName="单位"+pageNum+" 现职人员花名册";
                        export.copyHeed(true).sheetName(sheetName).rowIndex(0).count(1).dataList(maps).otherCellData(otherData).write();
                    }
                }catch (Exception e){
                }finally {
                    TexThreadLocal.clear();
                }
                    countDownLatch.countDown();

                });
            }

            try {
                countDownLatch.await();
                executorService.shutdown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        };

        sxssfExportOrdinaryContext.sxssfExportOrdinary().export(response, goExport);
        System.out.println("用时"+(System.currentTimeMillis()-l)+"毫秒");

    }


    //单位分组
    public void HMC_XZ_N(HttpServletResponse response){
        long l = System.currentTimeMillis();
        TexThreadLocal.setExTemplate(exTemplateMapper.getExTemplate("HMC_XZ_N"));
        ExecutorService executorService = TtlExecutors.getTtlExecutorService( Executors.newFixedThreadPool(3));
        CountDownLatch countDownLatch = new CountDownLatch(3);
        GoExport goExport =(export)->{
            for(int i=0;i<3;i++){
                int pageNum = i+1;
                int pageSize = 10000;
                executorService.execute(()->{
                   try {
                       List<Map<String, Object>> maps = personMapper.HMC_XZ(new Page<>(pageNum, pageSize, false));

                       synchronized (export){
                           List<IndexSetCellData> otherData = new ArrayList<>();
                           otherData.add(new IndexSetCellData(1,0,"单位"+pageNum,true));
                           DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.CHINA);;;
                           otherData.add(new IndexSetCellData(1,13,"数据日期:"+ LocalDate.now().format(timeFormatter),true));
                           export.copyHeed(true).interval(3).count(1).dataList(maps).otherCellData(otherData).write();
                       }
                   }catch (Exception e){

                   }finally {
                       TexThreadLocal.clear();
                   }

                    countDownLatch.countDown();
                });
            }

            try {
                countDownLatch.await();
                executorService.shutdown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        };

        sxssfExportOrdinaryContext.sxssfExportOrdinary().export(response, goExport);
        System.out.println("用时"+(System.currentTimeMillis()-l)+"毫秒");

    }


    //全量
    public void HMC_XZ(HttpServletResponse response){
        long l = System.currentTimeMillis();
        TexThreadLocal.setExTemplate(exTemplateMapper.getExTemplate("HMC_XZ"));

        GoExport goExport =(export)->{

            for(int i=0;i<10;i++){
                int pageNum = i+1;
                int pageSize = 10000;
                List<Map<String, Object>> maps = personMapper.HMC_XZ(new Page<>(pageNum, pageSize, false));
                export.dataList(maps).write();

            }



        };

        sxssfExportOrdinaryContext.sxssfExportOrdinary().export(response, goExport);
        System.out.println("用时"+(System.currentTimeMillis()-l)+"毫秒");

    }

    public void HMC_XZ66(HttpServletResponse response) {
        long l = System.currentTimeMillis();
        // -------------------------- 2核CPU专属核心配置 --------------------------
        int QUERY_THREAD_NUM = 4;
        int QUEUE_CAPACITY = 4;
        int BATCH_ROW_SIZE =20000;
        BlockingQueue<List<Map<String, Object>>> dataQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        AtomicInteger pageNum = new AtomicInteger(1);
        AtomicBoolean queryFinish = new AtomicBoolean(false);
        CountDownLatch queryLatch = new CountDownLatch(QUERY_THREAD_NUM);
        ExecutorService queryExecutor = TtlExecutors.getTtlExecutorService(new ThreadPoolExecutor(
                QUERY_THREAD_NUM,
                QUERY_THREAD_NUM,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(8),
                new ThreadPoolExecutor.AbortPolicy()
        ));
        // 全局中断标记：服务器断开时控制所有线程停止（验证核心）
        AtomicBoolean isInterrupted = new AtomicBoolean(false);
        // 修复lambda语法错误：用AtomicReference包装写入线程，满足final/有效final要求
        AtomicReference<Thread> writeThreadRef = new AtomicReference<>();

        try {
            // 1. 初始化导出模板，复用原有封装
            TexThreadLocal.setExTemplate(exTemplateMapper.getExTemplate("HMC_XZ"));

            // 2. 多线程查询：4个线程并行查询，分批生产数据到阻塞队列
            for (int i = 0; i < QUERY_THREAD_NUM; i++) {
                queryExecutor.execute(() -> {
                    try {
                        while (true) {
                            // 中断校验：服务器断开时立即退出循环
                            if (isInterrupted.get()) {
                                break;
                            }
                            int currentPage = pageNum.getAndIncrement();
                            List<Map<String, Object>> data = personMapper.HMC_XZ(new Page<>(currentPage, BATCH_ROW_SIZE, false));
                            if (data == null || data.isEmpty()) {
                                break;
                            }
                            // 队列put前校验中断，避免阻塞后无法响应停止
                            if (isInterrupted.get()) {
                                break;
                            }
                            dataQueue.put(data);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("查询线程被中断", e);
                    } finally {
                        queryLatch.countDown();
                        TexThreadLocal.clear();
                    }
                });
            }

            // 3. 单线程写入：实时消费阻塞队列数据，边消费边写入
            GoExport goExport = (export) -> {
                Thread writeThread = new Thread(() -> {
                    try {
                        while (true) {
                            // 中断校验：服务器断开时立即退出循环
                            if (isInterrupted.get()) {
                                break;
                            }
                            List<Map<String, Object>> batchData = dataQueue.poll(1, TimeUnit.SECONDS);
                            if (batchData != null && !batchData.isEmpty()) {
                                export.dataList(batchData).write();
                            }
                            // 原有结束条件 + 中断校验，双重保障
                            if (queryFinish.get() && dataQueue.isEmpty()) {
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("写入线程被中断", e);
                    }
                });
                // 存储写入线程引用，用于后续主动中断
                writeThreadRef.set(writeThread);
                writeThread.start();

                // ======== 还原你原始的代码逻辑：CountDownLatch阻塞等待 + 原始finally shutdown ========
                // 4. 等待所有查询线程完成，标记查询结束
                try {
                    queryLatch.await(); // 原始阻塞等待，不做循环超时
                    queryFinish.set(true); // 用set()方法修改标记，保证线程可见性
                } catch (InterruptedException e) {
                    // 捕获中断时，标记全局中断并强制关闭线程池
                    isInterrupted.set(true);
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("等待查询线程完成时被中断", e);
                } finally {
                    queryExecutor.shutdown(); // 还原原始的finally中shutdown()
                }
                // ================================= 还原结束 =================================


                // ======== 核心新增：等待写入线程（new Thread）完全结束，再走后续主线程逻辑 ========
                try {
                    writeThread.join(); // 阻塞等待写入线程执行完毕，无超时（和原始逻辑一致）
                } catch (InterruptedException e) {
                    isInterrupted.set(true); // 标记中断，让写入线程快速退出
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("等待写入线程结束时被中断", e);
                }
                // ==========================================================================
            };

            // 5. 执行导出逻辑，设置响应头解决中文乱码
            sxssfExportOrdinaryContext.sxssfExportOrdinary().export(response, goExport);

        } catch (Exception e) {
            // 捕获任意异常，立即标记中断，触发所有线程停止
            isInterrupted.set(true);
            throw new RuntimeException("HMC_XZ导出失败：" + e.getMessage(), e);
        } finally {
            // 核心兜底：服务器断开/任意场景下，强制停止所有线程（验证核心需求）
            isInterrupted.set(true); // 标记全局中断，让循环内线程立即退出
            // 获取写入线程并主动中断（new Thread停止核心方式）
            Thread writeThread = writeThreadRef.get();
            if (writeThread != null && writeThread.isAlive()) {
                writeThread.interrupt();
            }
            // 若线程池未关闭，强制shutdownNow中断所有查询线程
            if (!queryExecutor.isShutdown()) {
                queryExecutor.shutdownNow();
            }
            // 兜底释放资源
            dataQueue.clear();
            TexThreadLocal.clear();
            queryFinish.set(true);

            System.out.println("用时" + (System.currentTimeMillis() - l) + "毫秒");
        }
    }

    //全量数据
    public void HMC_XZ_33(HttpServletResponse response) {
        long l = System.currentTimeMillis();
        // -------------------------- 2核CPU专属核心配置 --------------------------
        int QUERY_THREAD_NUM = 4;
        int QUEUE_CAPACITY = 4;
        int BATCH_ROW_SIZE =20000;
        BlockingQueue<List<Map<String, Object>>> dataQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        AtomicInteger pageNum = new AtomicInteger(1);
        // 修复点1：用AtomicBoolean替代volatile局部变量，解决语法错误，保证线程可见性
        AtomicBoolean queryFinish = new AtomicBoolean(false);
        CountDownLatch queryLatch = new CountDownLatch(QUERY_THREAD_NUM);
        ExecutorService queryExecutor = TtlExecutors.getTtlExecutorService(new ThreadPoolExecutor(
                QUERY_THREAD_NUM,
                QUERY_THREAD_NUM,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(8),
                new ThreadPoolExecutor.AbortPolicy()
        ));

        try {
            // 1. 初始化导出模板，复用原有封装
            TexThreadLocal.setExTemplate(exTemplateMapper.getExTemplate("HMC_XZ"));

            // 2. 多线程查询：4个线程并行查询，分批生产数据到阻塞队列
            for (int i = 0; i < QUERY_THREAD_NUM; i++) {
                queryExecutor.execute(() -> {
                    try {
                        while (true) {
                            int currentPage = pageNum.getAndIncrement();
                            List<Map<String, Object>> data = personMapper.HMC_XZ(new Page<>(currentPage, BATCH_ROW_SIZE, false));
                            if (data == null || data.isEmpty()) {
                                break;
                            }
                            dataQueue.put(data);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("查询线程被中断", e);
                    } finally {
                        queryLatch.countDown();
                        TexThreadLocal.clear();
                    }
                });
            }

            // 3. 单线程写入：实时消费阻塞队列数据，边消费边写入
            GoExport goExport = (export) -> {
                new Thread(() -> {
                    try {
                        while (true) {
                            List<Map<String, Object>> batchData = dataQueue.poll(1, TimeUnit.SECONDS);
                            if (batchData != null && !batchData.isEmpty()) {
                                export.dataList(batchData).write();
                            }
                            // 修复点2：用AtomicBoolean的get()方法获取标记值，逻辑与原volatile完全一致
                            if (queryFinish.get() && dataQueue.isEmpty()) {
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("写入线程被中断", e);
                    }
                }).start();

                // 4. 等待所有查询线程完成，标记查询结束
                try {
                    queryLatch.await();
                    queryFinish.set(true); // 用set()方法修改标记，保证线程可见性
                } catch (InterruptedException e) {
                    throw new RuntimeException("等待查询线程完成时被中断", e);
                } finally {
                    queryExecutor.shutdown();
                }
            };

            // 5. 执行导出逻辑，设置响应头解决中文乱码
            sxssfExportOrdinaryContext.sxssfExportOrdinary().export(response, goExport);

        } catch (Exception e) {
            throw new RuntimeException("HMC_XZ导出失败：" + e.getMessage(), e);
        } finally {
            // 兜底释放资源
            queryFinish.set(true); // 强制标记查询结束
            if (!queryExecutor.isShutdown()) {
                queryExecutor.shutdownNow();
            }
            dataQueue.clear();
            TexThreadLocal.clear();
        }
        System.out.println("用时" + (System.currentTimeMillis() - l) + "毫秒");
    }


    //全量数据
    public void RY_CJ(HttpServletResponse response){
        long l = System.currentTimeMillis();
        TexThreadLocal.setExTemplate(exTemplateMapper.getExTemplate("RY_CJ"));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.CHINA);
        GoExport goExport =(export)->{
            export.dataList(personMapper.RY_CJ(null)).otherCellData(new IndexSetCellData(1, 0, LocalDate.now().format(timeFormatter), true)).write();
        };
        sxssfExportOrdinaryContext.sxssfExportOrdinary().export(response, goExport);
        System.out.println(System.currentTimeMillis()-l);
    }

    //数据分组
    public void RY_CJ_N(HttpServletResponse response){
        long l = System.currentTimeMillis();
        TexTemplate texTemplate = exTemplateMapper.getExTemplate("RY_CJ");
        texTemplate.setFileName(texTemplate.getFileName()+"(new)");
        TexThreadLocal.setExTemplate(texTemplate);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.CHINA);
        GoExport goExport =(export)->{
            List<Map<String, Object>> maps = personMapper.RY_CJ(new Page<>(1, 10000, false));
            export.copyHeed(true).interval(2).dataList(maps).otherCellData(new IndexSetCellData(1, 0, LocalDate.now().format(timeFormatter), true)).write();
            export.interval(2).dataList(personMapper.RY_CJ(new Page<>(3, 10000, false))).write();
        };
        sxssfExportOrdinaryContext.sxssfExportOrdinary().export(response, goExport);
        System.out.println(System.currentTimeMillis()-l);
    }


    public void RY_CJ_S(HttpServletResponse response){
        long l = System.currentTimeMillis();
        TexTemplate texTemplate = exTemplateMapper.getExTemplate("RY_CJ");
        texTemplate.setFileName(texTemplate.getFileName()+"(单位)");
        TexThreadLocal.setExTemplate(texTemplate);


        ExecutorService executorService = TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(15));
        CountDownLatch countDownLatch = new CountDownLatch(15);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.CHINA);
        IndexSetCellData indexSetCellData = new IndexSetCellData(1, 0,"日期： "+ LocalDate.now().format(timeFormatter), true);

        GoExport goExport =(export)->{
            export.getWorkbook().setSheetHidden(0,true);
            for(int i=0;i<15;i++) {
                int pageNum = i+1;
                int pageSize = 10000;
                executorService.execute(() -> {
                    try {
                        List<Map<String, Object>> maps = personMapper.RY_CJ(new Page<>(pageNum, pageSize, false));

                        synchronized (export) {
                            String sheetName="单位"+pageNum+" 人员成绩表";
                            export.rowIndex(0).copyHeed(true).sheetName(sheetName).dataList(maps).otherCellData(indexSetCellData).write();
                        }
                    }catch (Exception e){

                    }finally {
                        TexThreadLocal.clear();
                    }

                    countDownLatch.countDown();
                });


            }
            try {
                countDownLatch.await();
                executorService.shutdown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        };
        sxssfExportOrdinaryContext.sxssfExportOrdinary().export(response, goExport);
        System.out.println(System.currentTimeMillis()-l);
    }

    @Override
    public List<TexTemplateCell> head(String templateCode) {
        return texTemplateCellServiceImpl.query().eq("template_code",templateCode).list();
    }


    @Override
    public Page<Map<String, Object>> query(String templateCode,int pageNum,int pageSize) {
        Page<Map<String, Object>> page = new Page<>(pageNum, pageSize);
        List<Map<String, Object>> data =Collections.emptyList();
        if ("RY_CJ".equals(templateCode)) {
            data = personMapper.RY_CJ(page);
        }
        if ("HMC_XZ".equals(templateCode)) {
            data = personMapper.HMC_XZ(page);
        }
        if ("HMC_XZ_N".equals(templateCode)) {
            data = personMapper.HMC_XZ(page);
        }
        if ("HMC_XZ_S".equals(templateCode)) {
            data = personMapper.HMC_XZ(page);
        }
        page.setRecords(data);
        return page;
    }



    
    
  /*  public void HMC_XZ1(HttpServletResponse response){
        TexThreadLocal.setExTemplate(exTemplateMapper.getExTemplate("HMC_XZ"));
        ExportFileResponseUtil responseUtil = new ExportFileResponseUtil(response, TexThreadLocal.getExTemplate().getFileName(), "xlsx");
        //查询数据
        List<Map<String, Object>> dataList = personMapper.HMC_XZ();

        long startTime = System.currentTimeMillis();
        OSSUtil.downloadOSSInput(TexThreadLocal.getExTemplate().getTemplateUrl(), new OSSInputStream() {
            @Override
            public void CloseBefore(InputStream inputStream) throws IOException {
             //流输入poi
              XSSFWorkbook  xssfWorkbook = new XSSFWorkbook(inputStream);




                //暂时仅有1页
                int sheetAt = 0;
                //启始行
                startRow = Integer.parseInt(exTemplateCells.get(0).getCellRow());

                //初始(样式)行
                XSSFRow initRow = xssfWorkbook.getSheetAt(sheetAt).getRow(startRow);

                //SXSSF插数
                SXSSFWorkbook workbook = new SXSSFWorkbook(xssfWorkbook, 1000);
                SXSSFSheet sxssfSheet= workbook.getSheetAt(sheetAt);
                Row dataRow ;


                    for (int i = 0; i < dataList.size(); i++) {
                        dataRow=i==0?initRow:sxssfSheet.createRow(`startRow`+i);
                        if(i!=0){
                            //从初始(样式)行,复制样式
                            ExCellUtil.copyRowPOI(initRow, dataRow, false, false);
                        }

                        for (Map.Entry<Integer, String> entry : indexs.entrySet()) {
                            ExCellUtil.setCellValue(dataRow.getCell(entry.getKey()),dataList.get(i).get(entry.getValue()));
                        }

                    }


                //  workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
                sxssfSheet.setForceFormulaRecalculation(true);

                try(ServletOutputStream outputStream= responseUtil.getOutputStream()){
                    workbook.write(outputStream); }catch (Exception e){
                    //logger.error("人员信息(主数据)模板读取失败",e);
                }
            }

            @Override
            public void CloseAfter() throws Exception {}


        });
        // 执行代码块
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("程序执行耗时：" + timeElapsed + "毫秒");
        
        
        
    }*/



    public void PersonEx1(HttpServletResponse response) {


        ExportFileResponseUtil responseUtil = new ExportFileResponseUtil(response, "TEST", "xlsx");
        QueryWrapper<Person> objectQueryWrapper = new QueryWrapper<>();
        List<Person> people = personMapper.selectList(objectQueryWrapper);
        long startTime = System.currentTimeMillis();
        EasyExcel.write(responseUtil.getOutputStream(), Person.class).sheet(0).doWrite(people);
        // 执行代码块
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("程序执行耗时：" + timeElapsed + "毫秒");
    }

    public void PersonEx2(HttpServletResponse response) {
        ExportFileResponseUtil responseUtil = new ExportFileResponseUtil(response, "TEST", "xlsx");
        QueryWrapper<Person> objectQueryWrapper = new QueryWrapper<>();
        List<Person> people = personMapper.selectList(objectQueryWrapper);
        long startTime = System.currentTimeMillis();
        EasyExcel.write(responseUtil.getOutputStream(), Person.class).inMemory(true).sheet(0).doWrite(people);
        // 执行代码块
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("程序执行耗时：" + timeElapsed + "毫秒");
    }


    public void PersonSXSSF(HttpServletResponse response) {
        ExportFileResponseUtil responseUtil = new ExportFileResponseUtil(response, "TEST", "xlsx");
        XSSFWorkbook xssfWorkbook  =null;
        try (InputStream resourceAsStream= this.getClass().getClassLoader().getResourceAsStream("static/template/TEST.xlsx")){
            //流输入poi
            xssfWorkbook= new XSSFWorkbook(resourceAsStream);
        } catch (Exception e){

        }

        //查询数据

        //暂时仅有1页
        int sheetAt=0;
        //样式行
        int styleRow =1 ;
        //启始行
        int startRow =1;

        //取样式行
        XSSFRow row =  xssfWorkbook.getSheetAt(sheetAt).getRow(styleRow);
        //if(row==null){ logger.error("人员信息(主数据)模板，样式行缺失");return; }
        QueryWrapper<Person> objectQueryWrapper = new QueryWrapper<>();
        List<Person> people = personMapper.selectList(objectQueryWrapper);
        //SXSSF插数
        long startTime = System.currentTimeMillis();
        SXSSFWorkbook workbook = new SXSSFWorkbook(xssfWorkbook,1000);
        SXSSFSheet sheet = workbook.getSheetAt(sheetAt);
        SXSSFRow sxssfRow = null;

        //取行
        for(int i=0;i<people.size();i++){
            if(i==0){
                //插列
                row.getCell(1).setCellValue(people.get(i).getPernr());
                row.getCell(2).setCellValue(people.get(i).getName());
                row.getCell(3).setCellValue(people.get(i).getId_number());
                row.getCell(4).setCellValue(people.get(i).getSex());
                row.getCell(5).setCellValue(people.get(i).getBirthday());
                row.getCell(6).setCellValue(people.get(i).getMajor());
                row.getCell(7).setCellValue(people.get(i).getOrg());
            }else {
                sxssfRow = sheet.createRow((startRow + i));
                //从样式行复制样式
                ExCellUtil.copyRowPOI(row, sxssfRow, false, false);

            //插列
            sxssfRow.getCell(1).setCellValue(people.get(i).getPernr());
            sxssfRow.getCell(2).setCellValue(people.get(i).getName());
            sxssfRow.getCell(3).setCellValue(people.get(i).getId_number());
            sxssfRow.getCell(4).setCellValue(people.get(i).getSex());
            sxssfRow.getCell(5).setCellValue(people.get(i).getBirthday());
            sxssfRow.getCell(6).setCellValue(people.get(i).getMajor());
            sxssfRow.getCell(7).setCellValue(people.get(i).getOrg());

            }
          //  workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
            sheet.setForceFormulaRecalculation(true);

         /*   for(int j=0;j<empInfoSessceExportTemplate.length;j++){
                String txt=empinfos.get(empInfoSessceExportTemplate[j])==null?"":empinfos.get(empInfoSessceExportTemplate[j]).toString();
                if("序号".equals(empInfoSessceExportTemplate[j])){sxssfRow.getCell(0).setCellValue(i+1);
                }else { sxssfRow.getCell(j).setCellValue(FilingDataUtil.dataNullToString(txt)); }
            }*/
        }


        try(ServletOutputStream outputStream= response.getOutputStream()){
            workbook.write(outputStream); }catch (Exception e){
            //logger.error("人员信息(主数据)模板读取失败",e);
        }
        // 执行代码块
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("程序执行耗时：" + timeElapsed + "毫秒");


    }






    public void  insertdata(){

        for (int i = 14; i < 1000000; i++) {
            String pernr=String.valueOf(i);
            int length = 7-pernr.length();
            for(int j=0;j<length;j++){
                pernr="0"+pernr;
            }
            Person person = new Person(pernr,"张三",i%2==0?"男":"女","19980618","员工","","");
            personMapper.insert(person);
            System.out.println(person);



        }

     }





















}
