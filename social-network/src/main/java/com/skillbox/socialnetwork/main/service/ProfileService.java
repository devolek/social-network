package com.skillbox.socialnetwork.main.service;


import com.skillbox.socialnetwork.main.dto.person.request.UpdatePersonRequestDto;
import com.skillbox.socialnetwork.main.dto.post.request.AddPostRequestDto;
import com.skillbox.socialnetwork.main.dto.profile.request.ContactSupportRequestDto;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponseList;
import com.skillbox.socialnetwork.main.model.Person;

public interface ProfileService {
    BaseResponse getMyProfile(Person person);

    BaseResponse editMyProfile(Person person, UpdatePersonRequestDto request);

    BaseResponse deleteMyProfile(Person person);

    BaseResponse getUserById(int id, Person authorizedUser);

    Object getWallPosts(int id, int offset, int limit, Person authorizedUser);

    BaseResponse addPost(int id, long publishDate, AddPostRequestDto request);

    BaseResponseList searchPeople(String name,
                                  String surname,
                                  Integer ageFrom,
                                  Integer ageTo,
                                  String country,
                                  String city,
                                  Integer offset,
                                  Integer limit, int authorizedUserId);

    BaseResponse blockUser(int idOfABlockedUser, Person authorizedUser);

    BaseResponse unblockUser(int id, Person authorizedUser);

    BaseResponse sendMessageToSupport(ContactSupportRequestDto dto);
}
