import React, {useEffect, useRef, useState} from 'react';
import {
    ProCard,
    ProFormInstance,
    ProFormItem,
    ProFormSelect,
    ProFormText,
    ProFormTextArea,
    StepsForm,
} from '@ant-design/pro-components';
import FileUploadPage from "@/components/FileUpload";
import {
    addGeneratorUsingPost,
    getGeneratorVoByIdUsingGet,
    updateGeneratorUsingPost
} from "@/services/backend/generatorController";
import {message, UploadFile} from "antd";
import {history, useSearchParams} from "@@/exports";
import {COS_HOST} from "@/constants";
import PictureUploadPage from '@/components/PictureUploader';


/**
 * 生成器添加页面
 * @constructor
 */
const GeneratorAddPage: React.FC = () => {

    const [searchParams] = useSearchParams();
    const id = searchParams.get('id');
    const [oldData, setOldData] = useState<API.GeneratorEditRequest>();
    const formRef = useRef<ProFormInstance>();


    /**
     * 加载数据
     */
    const loadData = async () => {
        if (!id) {
            return;
        }
        try {
            const res = await getGeneratorVoByIdUsingGet({id});
            if (res.data) {
                const {distPath} = res.data ?? {};
                if (distPath) {
                    // @ts-ignore
                    res.data.distPath = [
                        {
                            uid: id,
                            name: "文件" + id,
                            status: "done",
                            url: COS_HOST + distPath,
                            response: distPath
                        } as UploadFile
                    ]
                }
                setOldData(res.data);
            }
        } catch (err: any) {
            message.error("加载数据失败," + err.message)
        }
    }

    /**
     * 更新
     * @param values
     */
    const doUpdate = async (values: API.GeneratorEditRequest) => {
        try {
            const res = await updateGeneratorUsingPost(values);
            if (res.data) {
                message.success("更新成功！");
                history.push(`/generator/detail/${id}`);
            }
        } catch (err: any) {
            message.error("更新失败！");
        }
    }


    /**
     * 创建
     * @param values
     */
    const doAdd = async (values: API.GeneratorAddRequest) => {
        try {
            const res = await addGeneratorUsingPost(values);
            if (res.data) {
                message.success("创建成功！");
                history.push(`/generator/detail/${res.data}`);
            }
        } catch (err: any) {
            message.error("创建失败！");
        }
    }

    /**
     * 提交
     * @param values
     */
    const doSubmit = async (values: API.GeneratorAddRequest) => {
        // 数据转换
        if (!values.modelConfig) {
            values.modelConfig = {};
        }
        if (!values.fileConfig) {
            values.fileConfig = {};
        }
        // 文件列表 转 url
        if (values.distPath && values.distPath.length > 0) {
            // @ts-ignore
            values.distPath = values.distPath[0].response;
        }
        if (id) {
            await doUpdate({
                id,
                ...values,
            })
        } else {
            await doAdd(values);
        }
    }

    useEffect(() => {
        if (!id) {
            return;
        }
        loadData();
    }, [id])

    return (
        <ProCard>
            {
                (!id || oldData) && (
                    < StepsForm<API.GeneratorAddRequest | API.GeneratorEditRequest>
                        formRef={formRef}
                        formProps={{
                            initialValues: oldData,
                        }}
                        onFinish={doSubmit}>
                        <StepsForm.StepForm
                            name="base"
                            title="基本信息"
                            onFinish={async () => {
                                console.log(formRef.current?.getFieldsValue());
                                return true;
                            }}>
                            <ProFormText name="name" label="名称" placeholder="请输入名称"/>
                            <ProFormTextArea name="description" label="描述" placeholder="请输入描述"/>
                            <ProFormText name="basePackage" label="基础包" placeholder="请输入基础包"/>
                            <ProFormText name="version" label="版本" placeholder="请输入版本"/>
                            <ProFormText name="author" label="作者" placeholder="请输入作者"/>
                            <ProFormSelect label="标签" mode="tags" name="tags" placeholder="请输入标签列表"/>

                            <ProFormItem name="picture" label="图片">
                                <PictureUploadPage biz="generator_picture"/>
                            </ProFormItem>

                        </StepsForm.StepForm>


                        <StepsForm.StepForm name="fileConfig" title="文件配置">
                            {/*todo 待补充*/}
                        </StepsForm.StepForm>
                        <StepsForm.StepForm name="modelConfig" title="模型配置">
                            {/*todo 待补充*/}
                        </StepsForm.StepForm>
                        <StepsForm.StepForm name="dist" title="生成器文件">
                            <ProFormItem name="distPath" label="产物包">
                                <FileUploadPage biz="generator_dist" description="请上传生成器文件压缩包"/>
                            </ProFormItem>
                        </StepsForm.StepForm>
                    </StepsForm>
                )}
        </ProCard>
    )
}


export default GeneratorAddPage;
