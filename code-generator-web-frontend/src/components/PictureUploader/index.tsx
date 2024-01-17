import {Flex, message, Upload, UploadProps} from "antd";
import React, {useState} from "react";
import {LoadingOutlined, PlusOutlined} from "@ant-design/icons";
import {uploadFileUsingPost} from "@/services/backend/fileController";
import {COS_HOST} from "@/constants";


interface Props {
  biz: string;
  onChange?: (url: string) => void;
  value?: string;
}

const PictureUploadPage: React.FC<Props> = (props) => {
  const {
    biz,
    onChange,
    value
  } = props

  const [loading, setLoading] = useState(false);

  const uploadProps: UploadProps = {
    name: 'file',
    listType: "picture-card",
    multiple: false,
    maxCount: 1,
    showUploadList: false,
    // 自定义上传
    customRequest: async (fileObj: any) => {
      setLoading(true);
      try {
        const res = await uploadFileUsingPost({biz}, {}, fileObj.file);
        const fullPath = COS_HOST + res.data;
        onChange?.(fullPath ?? '');
        fileObj.onSuccess(res.data);
      } catch (err: any) {
        message.error("上传失败:" + err.message)
        fileObj.onError(err)
      }
      setLoading(false);
    },
  };

  const uploadButton = (
    <div>
      {loading ? <LoadingOutlined/> : <PlusOutlined/>}
      <div style={{marginTop: 8}}>上传</div>
    </div>
  )

  return (

    <Flex>
      <Upload {...uploadProps}>
        {value ? <img src={value} alt="picture" style={{width: '100%'}}/> : uploadButton}
      </Upload>
    </Flex>
  );
};


export default PictureUploadPage;
