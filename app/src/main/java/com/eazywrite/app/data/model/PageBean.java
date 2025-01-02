package com.eazywrite.app.data.model;

import java.util.List;



public class PageBean {


    private Integer code;
    private String msg;
    private ResultDTO result;

    public static class ResultDTO {
        private Integer curpage;
        private Integer allnum;
        private List<NewslistDTO> newslist;


        public static class NewslistDTO {

            private String id;

            private String ctime;

            private String title;

            private String description;

            private String source;

            private String picUrl;
         
            private String url;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getCtime() {
                return ctime;
            }

            public void setCtime(String ctime) {
                this.ctime = ctime;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getPicUrl() {
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public Integer getCurpage() {
            return curpage;
        }

        public void setCurpage(Integer curpage) {
            this.curpage = curpage;
        }

        public Integer getAllnum() {
            return allnum;
        }

        public void setAllnum(Integer allnum) {
            this.allnum = allnum;
        }

        public List<NewslistDTO> getNewslist() {
            return newslist;
        }

        public void setNewslist(List<NewslistDTO> newslist) {
            this.newslist = newslist;
        }
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultDTO getResult() {
        return result;
    }

    public void setResult(ResultDTO result) {
        this.result = result;
    }
}
