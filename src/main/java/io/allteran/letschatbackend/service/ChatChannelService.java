package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.domain.ChatChannel;
import io.allteran.letschatbackend.domain.Role;
import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.exception.NotFoundException;
import io.allteran.letschatbackend.repo.ChatChannelRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatChannelService {
    private final ChatChannelRepo repo;
    private final ChatLanguageService languageService;
    private final ChatCategoryService categoryService;
    private final UserService userService;

    public List<ChatChannel> findAll() {
        return repo.findAll();
    }

    @Transactional
    public ChatChannel create(ChatChannel channel, User currentUser, ChatChannel.Type channelType) {
        if(channel.getName().isBlank()) {
            throw new EntityFieldException("ChatChannel missing name");
        }
        if(!currentUser.getRoles().contains(Role.ADMIN)) {
            if(!channel.getAuthorId().equals(currentUser.getId())) {
                throw new EntityFieldException("Author of channel is not current user or admin");
            }
        }
        if(userService.findById(channel.getAuthorId()).isEmpty()) {
            throw new NotFoundException("User not found [ID=" + channel.getAuthorId() + "]");
        }
        if(languageService.findById(channel.getLanguageId()).isEmpty()) {
            throw new NotFoundException("ChatLanguage not found [ID=" + channel.getLanguageId() + "]");
        }
        if(categoryService.findById(channel.getCategoryId()).isEmpty()) {
            throw new NotFoundException("ChatCategory not found [ID=" + channel.getCategoryId() + "]");
        }

        channel.setCreationDate(LocalDateTime.now());
        channel.setType(channelType);

        return repo.save(channel);
    }

    @Transactional
    public ChatChannel update(ChatChannel channel, User currentUser) {
        Optional<ChatChannel> optionalChannel = repo.findById(channel.getId());
        if(optionalChannel.isEmpty()) {
            throw new NotFoundException("ChatChannel not found [ID=" + channel.getId() + "]");
        }
        ChatChannel channelFromDb = optionalChannel.get();
        if(channel.getName().isBlank()) {
            throw new EntityFieldException("ChatChannel missing name");
        }
        if(!currentUser.getRoles().contains(Role.ADMIN)) {
            if(!channelFromDb.getAuthorId().equals(currentUser.getId()) || !channel.getAuthorId().equals(channelFromDb.getAuthorId())) {
                throw new EntityFieldException("Current user is not allowed to modify this ChatChannel. Existing [ChatChannel.authorId = " + channelFromDb.getAuthorId() +
                        "], modified [ChatChannel.authorId = " + channel.getAuthorId() + "], user [User.id = " + currentUser.getId() + "]");
            }
        }
        if(userService.findById(channel.getAuthorId()).isEmpty()) {
            throw new NotFoundException("User not found [ID=" + channel.getAuthorId() + "]");
        }
        if(languageService.findById(channel.getLanguageId()).isEmpty()) {
            throw new NotFoundException("ChatLanguage not found [ID=" + channel.getLanguageId() + "]");
        }
        if(categoryService.findById(channel.getCategoryId()).isEmpty()) {
            throw new NotFoundException("ChatCategory not found [ID=" + channel.getCategoryId() + "]");
        }

        BeanUtils.copyProperties(channel, channelFromDb, "id");

        return repo.save(channelFromDb);
    }
}
