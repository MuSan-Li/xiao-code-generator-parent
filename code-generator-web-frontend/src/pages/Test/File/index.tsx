import {Button, Card, Divider, Flex, message, Upload, UploadProps} from "antd";
import React from "react";
import {InboxOutlined} from "@ant-design/icons";
import {testDownloadFileUsingGet, testUploadFileUsingPost} from "@/services/backend/fileController";
import {COS_HOST} from "@/constants";
import {saveAs} from "file-saver";

const TestFilePage: () => void = () => {

  const [value, setValue] = React.useState<string>();

  const {Dragger} = Upload;

  const props: UploadProps = {
    name: 'file',
    multiple: false,
    maxCount: 1,
    // 自定义上传
    customRequest: async (fileObj: any) => {
      try {
        const res = await testUploadFileUsingPost({}, fileObj.file);
        fileObj.onSuccess(res.data);
        setValue(res.data);
      } catch (err: any) {
        message.error("上传失败:" + err.message)
        fileObj.onError(err)
      }
    }
  };

  return (

    <Flex>
      <Card title="文件上传">
        <Dragger {...props}>
          <p className="ant-upload-drag-icon">
            <InboxOutlined/>
          </p>
          <p className="ant-upload-text">Click or drag file to this area to upload</p>
          <p className="ant-upload-hint">
            Support for a single or bulk upload. Strictly prohibited from uploading company data or other
            banned files.
          </p>
        </Dragger>
      </Card>
      <Card title="文件下载">
        <Divider/>
        <div>文件地址：{COS_HOST + value}</div>
        <Divider/>
        <img src={COS_HOST + value} height={280} alt=''/>
        <Divider/>
        <Button onClick={
          async () => {
            // 返回的图片是二进制流
            const blob = await testDownloadFileUsingGet({filepath: value}, {responseType: "blob"});
            const fullPath = COS_HOST + value;
            saveAs(blob, fullPath.substring(fullPath.lastIndexOf("/") + 1));
          }
        }>下载文件</Button>
      </Card>
    </Flex>
  );
};


export default TestFilePage;
