import {Flex, message, UploadFile, UploadProps} from "antd";
import React, {useState} from "react";
import {InboxOutlined} from "@ant-design/icons";
import {uploadFileUsingPost} from "@/services/backend/fileController";
import Dragger from "antd/es/upload/Dragger";


interface Props {
    biz: string;
    onChange?: (fileList: UploadFile[]) => void;
    value?: UploadFile[];
    description?: string;
}

const FileUploadPage: React.FC<Props> = (props) => {
    const {
        biz,
        onChange,
        value,
        description
    } = props

    const [loading, setLoading] = useState(false);

    const uploadProps: UploadProps = {
        name: 'file',
        listType: "text",
        fileList: value,
        multiple: false,
        maxCount: 1,
        disabled: loading,
        onChange({fileList}) {
            onChange?.(fileList)
        },
        // 自定义上传
        customRequest: async (fileObj: any) => {
            setLoading(true);
            try {
                const res = await uploadFileUsingPost({
                        biz
                    },
                    {},
                    fileObj.file);
                fileObj.onSuccess(res.data);
            } catch (err: any) {
                message.error("上传失败:" + err.message)
                fileObj.onError(err)
            }
            setLoading(false);
        },
    };

    return (

        <Flex>
            <Dragger {...uploadProps}>
                <p className="ant-upload-drag-icon">
                    <InboxOutlined/>
                </p>
                <p className="ant-upload-text">点击或拖拽上传文件</p>
                <p className="ant-upload-hint">{description}</p>
            </Dragger>
        </Flex>
    );
};


export default FileUploadPage;
