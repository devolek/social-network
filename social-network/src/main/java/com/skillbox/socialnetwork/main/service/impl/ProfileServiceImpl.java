package com.skillbox.socialnetwork.main.service.impl;

import com.skillbox.socialnetwork.main.dto.person.request.UpdatePersonRequestDto;
import com.skillbox.socialnetwork.main.dto.person.response.PersonResponseFactory;
import com.skillbox.socialnetwork.main.dto.post.request.AddPostRequestDto;
import com.skillbox.socialnetwork.main.dto.post.response.PostResponseFactory;
import com.skillbox.socialnetwork.main.dto.profile.response.WallResponseFactory;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponseList;
import com.skillbox.socialnetwork.main.dto.universal.MessageResponseDto;
import com.skillbox.socialnetwork.main.dto.universal.ResponseFactory;
import com.skillbox.socialnetwork.main.model.*;
import com.skillbox.socialnetwork.main.model.enumerated.FriendshipCode;
import com.skillbox.socialnetwork.main.repository.*;
import com.skillbox.socialnetwork.main.service.PersonService;
import com.skillbox.socialnetwork.main.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {


    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final FriendshipStatusRepo friendshipStatusRepo;
    private final FriendshipRepository friendshipRepository;
    private final FileRepository fileRepository;
    private final PersonService personService;

    @Autowired
    public ProfileServiceImpl(PersonService personService, PostRepository postRepository,
                              TagRepository tagRepository, FriendshipStatusRepo friendshipStatusRepo,
                              FriendshipRepository friendshipRepository, FileRepository fileRepository) {
        this.personService = personService;
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.friendshipStatusRepo = friendshipStatusRepo;
        this.friendshipRepository = friendshipRepository;
        this.fileRepository = fileRepository;
    }


    @Override
    public BaseResponse getMyProfile(Person person) {
        log.info("IN getMyProfile person: {} found", person);
        return PersonResponseFactory.getPerson(person);
    }

    @Override
    public BaseResponse editMyProfile(Person person, UpdatePersonRequestDto request) {
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setBirthDate(request.getBirthDate());
        person.setPhone(request.getPhone());
        if (request.getPhotoId() != null) {
            person.setPhoto(fileRepository.findById(request.getPhotoId()).getRelativeFilePath());
        }
        person.setAbout(request.getAbout());
        person.setCity(request.getCity());
        person.setCountry(request.getCountry());

        log.info("IN editMyProfile person: {} edited with request: {} and saved to a database", person, request);
        return PersonResponseFactory.getPerson(personService.save(person));
    }

    @Override
    public BaseResponse deleteMyProfile(Person person) {
        personService.delete(person);
        log.info("IN deleteMyProfile user: {} deleted successfully", person);
        return ResponseFactory.responseOk();
    }

    @Override
    public BaseResponse getUserById(int id, Person authorizedUser) {
        Person profile = personService.findById(id);
        //эта строчка проверяет, заблокировал ли текущий авторизованный юзер юзера, которого ищем по id
        profile.setIsBlocked(friendshipRepository.isBlocked(authorizedUser, profile));
        BaseResponse result = PersonResponseFactory.getPerson(profile);
        log.info("IN getUserById user with id: {} {}  found.", id, result);
        return result;
    }

    @Override
    public BaseResponseList getWallPosts(int id, int offset, int limit) {
        Person person = personService.findById(id);

        List<Post> posts = person.getPosts();
        posts.sort(Comparator.comparing(Post::getTime).reversed());//сортирую по дате чтобы на стенке выводились сначала новые
        BaseResponseList result = WallResponseFactory.getWall(posts, offset, limit, person);

        log.info("IN getWallPosts posts {} have found", result);
        return result;
    }

    @Override
    public BaseResponse addPost(int id, long publishDate, AddPostRequestDto request) {
        Person person = personService.findById(id);
        Post post = new Post();
        post.setAuthor(person);
        if (publishDate == 0) {
            post.setTime(new Date());
        } else {
            post.setTime(new Date(publishDate));
        }
        post.setTitle(request.getTitle());
        post.setPostText(request.getText());
        post.setIsBlocked(false);
        //collections initialized to avoid nullPointer in PostResponseFactoryMethods
        post.setLikes(new ArrayList<>());
        post.setComments(new ArrayList<>());
        post.setFiles(new ArrayList<>());
        post.setBlockHistories(new ArrayList<>());

        Post savedPost = postRepository.save(post);
        //tags
        List<Tag> tags = new ArrayList<>();
        if (request.getTags().size() != 0) {            //если тегов нет в запросе, блок пропускается
            request.getTags().forEach(tag -> {
                Tag postTag;
                if (tagRepository.existsByTagIgnoreCase(tag)) {
                    postTag = tagRepository.findFirstByTagIgnoreCase(tag);
                } else {
                    postTag = new Tag();
                    postTag.setTag(tag);
                }
                tags.add(postTag);
            });
            savedPost.setTags(tags);
        }
        Post result = postRepository.save(savedPost);
        log.info("IN addPost post: {} added with tags: {} successfully", result, tags);
        return PostResponseFactory.getSinglePost(result, person);
    }

    @Override
    public BaseResponseList searchPeople(String name, String surname, Integer ageFrom, Integer ageTo, String country,
                                         String city, Integer offset, Integer limit) {
        // превращаю из локалдейт в дату ибо spring jpa не может в query воспринимать LocalDate и принимает только Date
        Date dateTo = Date.from(LocalDate.now().minusYears(ageFrom).plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());//плюс день для верхней даты и минус день
        Date dateFrom = Date.from(LocalDate.now().minusYears(ageTo).minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());//для нижней т.к. between строгое сравнение.(<>)
        List<Person> result = personService.search(name, surname, dateFrom, dateTo, city, country);

        log.info("IN searchPeople by parameters: name {}, surname {}, ageFrom {}, ageTo {}, country {}, city {} found {} result",
                name, surname, ageFrom, ageTo, country, city, result.size());
        return PersonResponseFactory.getPersons(result, offset, limit);
    }

    @Override
    public BaseResponse blockUser(int idOfABlockedUser, Person authorizedUser) {
        Person profileToBlock = personService.findById(idOfABlockedUser);
        FriendshipStatus status = new FriendshipStatus();
        status.setCode(FriendshipCode.BLOCKED);
        status.setName("name");//Для чего поле ??
        status.setTime(new Date());
        Friendship relation = new Friendship();
        relation.setSrcPerson(authorizedUser);
        relation.setDstPerson(profileToBlock);
        relation.setStatus(friendshipStatusRepo.save(status));

        friendshipRepository.save(relation);
        log.info("IN blockUser user: {} blocked user {}", authorizedUser.getEmail(), profileToBlock.getEmail());
        return ResponseFactory.responseOk();

    }

    @Override
    public BaseResponse unblockUser(int id, Person authorizedUser) {
        Person profileToBlock = personService.findById(id);
        friendshipRepository.delete(friendshipRepository.findRelation(authorizedUser, profileToBlock, FriendshipCode.BLOCKED));
        log.info("IN unblockUser user: {} unblocked user {}", authorizedUser.getEmail(), profileToBlock.getEmail());
        return ResponseFactory.responseOk();
    }

}
