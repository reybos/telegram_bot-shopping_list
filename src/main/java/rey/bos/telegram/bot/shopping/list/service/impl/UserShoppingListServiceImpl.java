package rey.bos.telegram.bot.shopping.list.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import rey.bos.telegram.bot.shopping.list.io.entity.JoinRequest;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.io.repository.JoinRequestRepository;
import rey.bos.telegram.bot.shopping.list.io.repository.UserRepository;
import rey.bos.telegram.bot.shopping.list.io.repository.UserShoppingListRepository;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserShoppingListServiceImpl implements UserShoppingListService {

    private final UserShoppingListRepository userShoppingListRepository;
    private final JoinRequestRepository joinRequestRepository;
    private final UserRepository userRepository;

    @Override
    public List<UserShoppingListGroupParams> findActiveGroupByListId(long listId) {
        return userShoppingListRepository.findActiveGroupByListId(listId);
    }

    @Override
    public UserShoppingList findActiveUserShoppingList(long userId) {
        List<UserShoppingList> lists = userShoppingListRepository.findByUserIdAndActive(userId, true);
        checkListCount(lists, userId);
        return lists.get(0);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void changeSenderActiveList(JoinRequest joinRequest, UserShoppingList newList) {
        joinRequest.setApproved(true);
        joinRequestRepository.save(joinRequest);
        Optional<User> userO = userRepository.findById(joinRequest.getUserId());
        if (userO.isEmpty()) {
            throw new NoSuchElementException("The user with the id=" + joinRequest.getUserId() + " was not found");
        }
        User sender = userO.get();
        List<UserShoppingList> currSenderLists = userShoppingListRepository.findByUserIdAndActive(sender.getId(), true);
        checkListCount(currSenderLists, sender.getId());
        UserShoppingList currSenderList = currSenderLists.get(0);
        currSenderList.setActive(false);
        userShoppingListRepository.save(currSenderList);
        userShoppingListRepository.save(
            UserShoppingList.builder()
                .active(true)
                .owner(false)
                .shoppingListId(newList.getShoppingListId())
                .userId(sender.getId())
                .build()
        );
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public UserShoppingList restoreMainList(UserDto user, UserShoppingList activeList) {
        activeList.setActive(false);
        userShoppingListRepository.save(activeList);
        List<UserShoppingList> lists = userShoppingListRepository.findByUserIdAndOwner(user.getId(), true);
        checkListCount(lists, user.getId());
        UserShoppingList ownList = lists.get(0);
        ownList.setActive(true);
        return userShoppingListRepository.save(ownList);
    }

    private void checkListCount(List<UserShoppingList> lists, long userId) {
        if (CollectionUtils.isEmpty(lists) || lists.size() != 1) {
            throw new IllegalStateException(
                "The number of active lists for a user with id = " + userId + " is not equal to 1"
            );
        }
    }

}
