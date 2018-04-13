package com.shrill.version.controller;


import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ApiModel(value = "VersionCtroller-Test")
public class VersionCtroller {


    @RequestMapping(path = "/hello", headers = "apt-version=1", method = RequestMethod.GET)
    @ApiOperation(value = "hello world")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "apt-version", allowableValues = "1,2", value = "版本号", required = true, paramType = "header", dataType = "string")
    })
    public String hello1() {

        return "hello world version 1";

    }

    @RequestMapping(path = "/hello", headers = "apt-version=2", method = RequestMethod.GET)
    @ApiOperation(value = "hello world")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "apt-version", allowableValues = "1,2", value = "版本号", required = true, paramType = "header", dataType = "string")
    })
    public String hello2() {

        return "hello world version 2";

    }

    @RequestMapping(path = "/hello/2", headers = {"Content-Type=application/v1+json", "Content-Type=application/v2+json"}, method = RequestMethod.GET)
    @ApiOperation(value = "hello world")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Content-Type", allowableValues = "application/v1+json,application/v2+json,application/v3+json", value = "版本号", required = true, paramType = "header", dataType = "string")
    })
    public String hello33() {

        return "hello world version v1 v2";

    }

    @RequestMapping(path = "/hello/2", headers = "Content-Type=application/v3+json", method = RequestMethod.GET)
    @ApiOperation(value = "hello world")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Content-Type", allowableValues = "application/v3+json", value = "版本号", required = true, paramType = "header", dataType = "string")
    })
    public String hello4() {

        return "hello world version 3";

    }
}
