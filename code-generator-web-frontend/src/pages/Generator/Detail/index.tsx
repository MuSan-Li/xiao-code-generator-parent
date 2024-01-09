import React, {useEffect, useRef, useState} from 'react';
import {PageContainer, ProFormInstance,} from '@ant-design/pro-components';
import {downloadGeneratorByIdUsingGet, getGeneratorVoByIdUsingGet} from "@/services/backend/generatorController";
import {Button, Card, Col, Image, message, Row, Space, Tabs, Tag} from "antd";
import {Typography} from 'antd/lib';
import FileConfig from "@/pages/Generator/Detail/components/FileConfig";
import ModelConfig from "@/pages/Generator/Detail/components/ModelConfig";
import AuthorInfo from "@/pages/Generator/Detail/components/AuthorInfo";
import {DownloadOutlined, EditOutlined} from "@ant-design/icons";
import {saveAs} from "file-saver";
import moment from "moment";
import {Link, useModel, useParams, useSearchParams} from "@@/exports";


/**
 * 生成器添加页面
 * @constructor
 */
const GeneratorAddPage: React.FC = () => {

    const [searchParams] = useSearchParams();
    const {id} = useParams();
    const [data, setData] = useState<API.GeneratorVO>({});
    const formRef = useRef<ProFormInstance>();
    const [loading, setLoading] = useState<boolean>(false);

    const {initialState} = useModel('@@initialState');
    const {currentUser} = initialState ?? {};
    const my = currentUser?.id === data?.userId;


    /**
     * 加载数据
     */
    const loadData = async () => {
        if (!id) {
            return;
        }
        try {
            const res = await getGeneratorVoByIdUsingGet({id});
            setData(res.data ?? {});
        } catch (err: any) {
            message.error("加载数据失败," + err.message)
        }
    }

    useEffect(() => {
        if (!id) {
            return;
        }
        loadData();
    }, [id])


    /**
     * 标签列表视图
     * @param tags
     */
    const tagListView = (tags?: string[]) => {
        if (!tags) {
            return <></>;
        }

        return (
            <div style={{marginBottom: 8}}>
                {tags.map((tag: string) => {
                    return <Tag key={tag}>{tag}</Tag>;
                })}
            </div>
        );
    };

    /**
     * 下载按钮
     */
    const downloadButton = data.distPath && currentUser && (
        <Button
            icon={<DownloadOutlined/>}
            onClick={async () => {
                const blob = await downloadGeneratorByIdUsingGet(
                    {
                        id: data.id,
                    },
                    {
                        responseType: 'blob',
                    },
                );
                // 使用 file-saver 来保存文件
                const fullPath = data.distPath || '';
                saveAs(blob, fullPath.substring(fullPath.lastIndexOf('/') + 1));
            }}
        >
            下载
        </Button>
    );

    /**
     * 编辑按钮
     */
    const editButton = my && (
        <Link to={`/generator/update?id=${data.id}`}>
            <Button icon={<EditOutlined/>}>编辑</Button>
        </Link>
    );

    return (
        <PageContainer title={<></>} loading={loading}>
            <Card>
                <Row justify="space-between" gutter={[32, 32]}>
                    <Col flex="auto">
                        <Space size="large" align="center">
                            <Typography.Title level={4}>{data.name}</Typography.Title>
                            {tagListView(data.tags)}
                        </Space>
                        <Typography.Paragraph>{data.description}</Typography.Paragraph>
                        <Typography.Paragraph type="secondary">
                            创建时间：{moment(data.createTime).format('YYYY-MM-DD hh:mm:ss')}
                        </Typography.Paragraph>
                        <Typography.Paragraph type="secondary">基础包：{data.basePackage}</Typography.Paragraph>
                        <Typography.Paragraph type="secondary">版本：{data.version}</Typography.Paragraph>
                        <Typography.Paragraph type="secondary">作者：{data.author}</Typography.Paragraph>
                        <div style={{marginBottom: 24}}/>
                        <Space size="middle">
                            <Button type="primary">立即使用</Button>
                            {downloadButton}
                            {editButton}
                        </Space>
                    </Col>
                    <Col flex="320px">
                        <Image src={data.picture}/>
                    </Col>
                </Row>
            </Card>
            <div style={{marginBottom: 24}}/>
            <Card>
                <Tabs
                    size="large"
                    defaultActiveKey={'fileConfig'}
                    onChange={() => {
                    }}
                    items={[
                        {
                            key: 'fileConfig',
                            label: '文件配置',
                            children: <FileConfig data={data}/>,
                        },
                        {
                            key: 'modelConfig',
                            label: '模型配置',
                            children: <ModelConfig data={data}/>,
                        },
                        {
                            key: 'userInfo',
                            label: '作者信息',
                            children: <AuthorInfo data={data}/>,
                        },
                    ]}
                />
            </Card>
        </PageContainer>
    )
}


export default GeneratorAddPage;
