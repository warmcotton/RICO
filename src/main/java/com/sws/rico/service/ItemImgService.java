package com.sws.rico.service;

import com.sws.rico.entity.Item;
import com.sws.rico.entity.ItemImg;
import com.sws.rico.exception.FileException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemImgService {
    @Value("${img.location}") private String imgLocation;

    protected List<ItemImg> saveImage(Item item, List<MultipartFile> multi) {
        List<ItemImg> itemImgList = new ArrayList<>();
        int count = 0;
        String repImgYn ="Y";
        for (MultipartFile file : multi) {
            if (count != 0) repImgYn="N";
            String oriImgName = file.getOriginalFilename();
            String extension = oriImgName.substring(oriImgName.lastIndexOf("."));
            String savedFileName = UUID.randomUUID() + extension;
            String imgUrl = "/images/rico/"+savedFileName;
            try {
                FileOutputStream fos = new FileOutputStream(imgLocation+"/"+savedFileName);
                fos.write(file.getBytes());
                fos.close();
            } catch (IOException exception) {
                throw new FileException("file save failed");
            }
            itemImgList.add(ItemImg.createItemImg(savedFileName, oriImgName, imgUrl, repImgYn, item));
            count++;
        }
        return itemImgList;
    }
}
