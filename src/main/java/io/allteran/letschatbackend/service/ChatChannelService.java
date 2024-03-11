package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.domain.ChatChannel;
import io.allteran.letschatbackend.domain.Role;
import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.exception.AccessException;
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
        if (channel.getName().isBlank()) {
            throw new EntityFieldException("ChatChannel missing name");
        }
        if (repo.findByName(channel.getName()) != null) {
            throw new EntityFieldException("ChatChannel should have unique name [name=" + channel.getName() + "]");
        }
        if (!currentUser.getRoles().contains(Role.ADMIN)) {
            channel.setAuthorId(currentUser.getId());
        } else {
            if (channel.getAuthorId() == null || channel.getAuthorId().isEmpty()) {
                channel.setAuthorId(currentUser.getId());
            }
        }
        if (userService.findById(channel.getAuthorId()).isEmpty()) {
            throw new NotFoundException("User not found [ID=" + channel.getAuthorId() + "]");
        }
        if (languageService.findById(channel.getLanguageId()).isEmpty()) {
            throw new NotFoundException("ChatLanguage not found [ID=" + channel.getLanguageId() + "]");
        }
        if (categoryService.findById(channel.getCategoryId()).isEmpty()) {
            throw new NotFoundException("ChatCategory not found [ID=" + channel.getCategoryId() + "]");
        }

        channel.setCreationDate(LocalDateTime.now());
        channel.setType(channelType);

        return repo.save(channel);
    }

    @Transactional
    public ChatChannel update(String idFromDb, ChatChannel channel, User currentUser) {
        Optional<ChatChannel> optionalChannel = repo.findById(idFromDb);
        if (optionalChannel.isEmpty()) {
            throw new NotFoundException("ChatChannel not found [ID=" + channel.getId() + "]");
        }
        ChatChannel channelFromDb = optionalChannel.get();
        if (channel.getName().isBlank()) {
            throw new EntityFieldException("ChatChannel missing name");
        }
        if (!channel.getName().equals(channelFromDb.getName())) {
            if (repo.findByName(channel.getName()) != null) {
                throw new EntityFieldException("ChatChannel should have unique name [name=" + channel.getName() + "]");
            }
        }

        if (!channel.getAuthorId().isEmpty()) {
            if (!currentUser.getRoles().contains(Role.ADMIN)) {
                channel.setAuthorId(channelFromDb.getAuthorId());
            }
        }
        if (languageService.findById(channel.getLanguageId()).isEmpty()) {
            throw new NotFoundException("ChatLanguage not found [ID=" + channel.getLanguageId() + "]");
        }
        if (categoryService.findById(channel.getCategoryId()).isEmpty()) {
            throw new NotFoundException("ChatCategory not found [ID=" + channel.getCategoryId() + "]");
        }

        BeanUtils.copyProperties(channel, channelFromDb, "id");

        return repo.save(channelFromDb);
    }

    @Transactional
    public void delete(String id, User user) {

        Optional<ChatChannel> optionalChannel = repo.findById(id);
        if (optionalChannel.isEmpty()) {
            throw new NotFoundException("ChatChannel not found [ID=" + id + "]");
        }
        if (!user.getRoles().contains(Role.ADMIN)) {
            if (!user.getId().equals(optionalChannel.get().getAuthorId())) {
                throw new AccessException("Current user not allowed to delete this channel");
            }
        }

        repo.deleteById(id);
    }

    @Transactional
    public void addJoinedUser(String userId, String channelId) {
        Optional<ChatChannel> opt = repo.findById(channelId);
        if (opt.isEmpty()) {
            throw new NotFoundException("Can't find chat channel with id [{}]", opt);
        }
        ChatChannel channel = opt.get();
        channel.addSubscriber(userId);

        repo.save(channel);
    }

    public Long countSubscribers(String channelId) {
        Optional<ChatChannel> channelOpt = repo.findById(channelId);
        if (channelOpt.isEmpty()) {
            throw new NotFoundException("Can't find chat channel with ID [{}]", channelId);
        }
        if (channelOpt.get().getSubscribers() == null) {
            return 0L;
        }
        return (long) channelOpt.get().getSubscribers().size();
    }
}
