package com.example.postservice.controller;
import com.example.postservice.domain.po.Tag;
import com.example.postservice.domain.po.TagOption;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TagController {

    @GetMapping("/tags")
    public Response<Map<String, List<Tag>>> getAllTag() {
        return null;
    }

    @GetMapping("/tagsAndOptions")
    public Response<Map<String, Object>> getTagsAndOptions() {
        return null;
    }

    @GetMapping("/tag")
    public Response<Map<String, Object>> getTag(@RequestParam("label") String tagLabel, @RequestParam("page") Integer currentPage) {
        return null;
    }

    @PostMapping("/sys-ctrl/tag")
    public Response<Map<String, Tag>> addTag(@RequestBody Tag tag) {
        return null;
    }

    @PutMapping("/sys-ctrl/tag")
    public Response<List<Tag>> updateTag(@RequestBody Tag tag) {
        return null;
    }

    @DeleteMapping("/sys-ctrl/tag")
    public Response<List<Tag>> deleteTag(@RequestBody Tag tag) {
        return null;
    }

    @DeleteMapping("/sys-ctrl/tagForce")
    public Response<List<Tag>> forceDeleteTag(@RequestBody Tag tag) {
        return null;
    }

    @GetMapping("/sys-ctrl/tagOption")
    public Response<List<TagOption>> getAllTagOptions() {
        return null;
    }

    @PostMapping("/sys-ctrl/tagOption")
    public Response<Map<String, TagOption>> addTagOption(@RequestBody TagOption tagOption) {  // 这个操作不需要权限吗
        return null;
    }

    @PutMapping("/sys-ctrl/tagOption")
    public Response<Map<String, TagOption>> updateTagOption(@RequestBody TagOption tagOption) {
        return null;
    }

    @DeleteMapping("/sys-ctrl/tagOption")
    public Response<List<TagOption>> deleteTagOption(@RequestBody TagOption tagOption) {
        return null;
    }

    @DeleteMapping("/sys-ctrl/tagOptionForce")
    public Response<Object> forceDeleteTagOption(@RequestBody TagOption tagOption) {
        return null;
    }
}
