package com.skillbox.socialnetwork.main.service.impl;

import com.skillbox.socialnetwork.main.aspect.MethodLogWithTime;
import com.skillbox.socialnetwork.main.dto.person.request.UpdatePersonRequestDto;
import com.skillbox.socialnetwork.main.dto.person.response.PersonResponseFactory;
import com.skillbox.socialnetwork.main.dto.post.request.AddPostRequestDto;
import com.skillbox.socialnetwork.main.dto.post.response.PostResponseFactory;
import com.skillbox.socialnetwork.main.dto.profile.request.ContactSupportRequestDto;
import com.skillbox.socialnetwork.main.dto.profile.response.WallResponseFactory;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponseList;
import com.skillbox.socialnetwork.main.dto.universal.Dto;
import com.skillbox.socialnetwork.main.dto.universal.ResponseFactory;
import com.skillbox.socialnetwork.main.model.*;
import com.skillbox.socialnetwork.main.model.enumerated.FriendshipCode;
import com.skillbox.socialnetwork.main.model.enumerated.NotificationCode;
import com.skillbox.socialnetwork.main.repository.*;
import com.skillbox.socialnetwork.main.service.EmailService;
import com.skillbox.socialnetwork.main.service.NotificationService;
import com.skillbox.socialnetwork.main.service.PersonService;
import com.skillbox.socialnetwork.main.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final EmailService emailService;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final FriendshipStatusRepo friendshipStatusRepo;
    private final FriendshipRepository friendshipRepository;
    private final FileRepository fileRepository;
    private final PersonService personService;
    private final DialogRepository dialogRepository;
    private final NotificationService notificationService;

    @Autowired
    public ProfileServiceImpl(EmailService emailService, PersonService personService, PostRepository postRepository,
            TagRepository tagRepository, FriendshipStatusRepo friendshipStatusRepo,
            FriendshipRepository friendshipRepository, FileRepository fileRepository, DialogRepository dialogRepository, NotificationService notificationService)
    {
        this.emailService = emailService;
        this.personService = personService;
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.friendshipStatusRepo = friendshipStatusRepo;
        this.friendshipRepository = friendshipRepository;
        this.fileRepository = fileRepository;
        this.dialogRepository = dialogRepository;
        this.notificationService = notificationService;
    }


    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "MyProfile loaded")
    public BaseResponse getMyProfile(Person person)
    {
        return PersonResponseFactory.getPerson(person);
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Profile edited")
    public BaseResponse editMyProfile(Person person, UpdatePersonRequestDto request)
    {
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setBirthDate(request.getBirthDate());
        person.setPhone(request.getPhone());
        if (request.getPhotoId() != null)
        {
            person.setPhoto(fileRepository.findById(request.getPhotoId()).getRelativeFilePath());
        }
        person.setAbout(request.getAbout());
        person.setCity(request.getCity());
        person.setCountry(request.getCountry());

        return PersonResponseFactory.getPerson(personService.save(person));
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Profile deleted")
    public BaseResponse deleteMyProfile(Person person)
    {
        personService.delete(person);
        return ResponseFactory.responseOk();
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "getUserById")
    public BaseResponse getUserById(int id, Person authorizedUser)
    {
        Person profile = personService.findById(id);
        profile.setIsBlocked(friendshipRepository.isBlocked(authorizedUser, profile));
        profile.setAreYouBlocked(friendshipRepository.areYouBlocked(authorizedUser, profile));
        boolean isMyProfile = id == authorizedUser.getId();
        Dto response = PersonResponseFactory.getPersonById(profile,
                friendshipRepository.isFriend(authorizedUser, profile), isMyProfile);
        return ResponseFactory.getBaseResponse(response);
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Wall posts loaded")
    public Object getWallPosts(int id, int offset, int limit, Person authorizedUser)
    {
        Person person = personService.findById(id);

        List<Post> posts = person.getPosts();
        posts.sort(Comparator.comparing(Post::getTime)
                .reversed());//сортирую по дате чтобы на стенке выводились сначала новые
        BaseResponseList result = WallResponseFactory.getWall(posts, offset, limit, authorizedUser);

        return result;
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "New wall post added")
    public BaseResponse addPost(int id, long publishDate, AddPostRequestDto request)
    {
        Person person = personService.findById(id);
        Post post = new Post();
        post.setAuthor(person);
        if (publishDate == 0 || new Date(publishDate).before(new Date()))
        {
            post.setTime(new Date());
        } else
        {
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
        Set<Tag> tags = new HashSet<>();
        if (request.getTags().size() != 0)
        {            //если тегов нет в запросе, блок пропускается
            request.getTags().forEach(tag -> {
                Tag postTag;
                if (tagRepository.existsByTagIgnoreCase(tag))
                {
                    postTag = tagRepository.findFirstByTagIgnoreCase(tag);
                } else
                {
                    postTag = new Tag();
                    postTag.setTag(tag);
                }
                tags.add(postTag);
            });
            savedPost.setTags(tags);
        }
        Post result = postRepository.save(savedPost);

        //Notification
        person.getFriendshipsDst().stream().map(Friendship::getSrcPerson).forEach(p -> notificationService
                .addNotification(person, p, NotificationCode.POST, "Пользователь добавил новый пост \"" + post
                        .getTitle() + "\""));

        return PostResponseFactory.getSinglePost(result, person);
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Search people")
    public BaseResponseList searchPeople(String name, String surname, Integer ageFrom, Integer ageTo, String country,
            String city, Integer offset, Integer limit, int authorizedUserId)
    {
        // превращаю из локалдейт в дату ибо spring jpa не может в query воспринимать LocalDate и принимает только Date
        Date dateTo = Date.from(LocalDate.now().minusYears(ageFrom).plusDays(1).atStartOfDay(ZoneId.systemDefault())
                .toInstant());//плюс день для верхней даты и минус день
        Date dateFrom = Date.from(LocalDate.now().minusYears(ageTo).minusDays(1).atStartOfDay(ZoneId.systemDefault())
                .toInstant());//для нижней т.к. between строгое сравнение.(<>)
        List<Person> result = personService.search(name, surname, dateFrom, dateTo, city, country);
        return ResponseFactory.getBaseResponseListWithLimit(result.stream()
                .map(r -> {
                    r.setIsBlocked(friendshipRepository.isBlocked(personService.findById(authorizedUserId), r));
                    r.setAreYouBlocked(friendshipRepository.areYouBlocked(personService.findById(authorizedUserId), r));
                    return PersonResponseFactory.getPersonWithFriendshipDto(r,
                            friendshipRepository.isFriend(personService.findById(authorizedUserId), r));
                })
                .collect(Collectors.toList()), offset, limit);
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "User blocked")
    public BaseResponse blockUser(int idOfABlockedUser, Person authorizedUser)
    {
        Person profileToBlock = personService.findById(idOfABlockedUser);
        FriendshipStatus status = new FriendshipStatus();
        status.setCode(FriendshipCode.BLOCKED);
        status.setTime(new Date());

        if (friendshipRepository.isFriend(authorizedUser, profileToBlock))
        {
            Friendship srcRelation = friendshipRepository
                    .findRelation(authorizedUser, profileToBlock, FriendshipCode.FRIEND);
            Friendship dstRelation = friendshipRepository
                    .findRelation(profileToBlock, authorizedUser, FriendshipCode.FRIEND);
            dstRelation.setStatus(
                    friendshipStatusRepo.save(FriendshipStatus.builder()
                            .code(FriendshipCode.SUBSCRIBED)
                            .time(new Date())
                            .build()));
            srcRelation.setStatus(friendshipStatusRepo.save(status));
            friendshipRepository.save(dstRelation);
            friendshipRepository.save(srcRelation);
        } else
        {
            Friendship relation = new Friendship();
            relation.setSrcPerson(authorizedUser);
            relation.setDstPerson(profileToBlock);
            relation.setStatus(friendshipStatusRepo.save(status));

            friendshipRepository.save(relation);
        }
        //заморозка диалогов с заблокированным
        dialogRepository.updateDialogStatus(getDialogIdByPeople(authorizedUser, profileToBlock), true);

        return ResponseFactory.responseOk();

    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "User unblocked")
    public BaseResponse unblockUser(int id, Person authorizedUser)
    {
        Person profileToBlock = personService.findById(id);
        friendshipRepository
                .delete(friendshipRepository.findRelation(authorizedUser, profileToBlock, FriendshipCode.BLOCKED));

        //разморозка диалогов
        dialogRepository.updateDialogStatus(getDialogIdByPeople(authorizedUser, profileToBlock), false);

        return ResponseFactory.responseOk();
    }

    @Override
    @MethodLogWithTime(fullMessage = "Support message sent")
    public BaseResponse sendMessageToSupport(ContactSupportRequestDto dto)
    {
        String messageText = new StringBuilder()
                .append("Пользователь с именем ")
                .append(dto.getName())
                .append(" и email ")
                .append(dto.getEmail())
                .append(" отправил запрос:\n")
                .append(dto.getMessage())
                .toString();
        emailService.sendSimpleMessage(
                "skillboxsocial@gmail.com",
                messageText,
                "Обращение в службу поддержки от " + LocalDate.now()
                        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        return ResponseFactory.responseOk();
    }

    private Integer getDialogIdByPeople(Person p1, Person p2){
        try
        {
            return p1.getDialogs().stream()
                    .filter(dialog -> dialog.getPeople().contains(p1) && dialog.getPeople()
                            .contains(p2)).findFirst().get().getId();
        } catch (NoSuchElementException ex){
            return -1;
        }
    }

}
