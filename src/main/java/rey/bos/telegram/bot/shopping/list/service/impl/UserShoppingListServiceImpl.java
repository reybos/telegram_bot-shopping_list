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
import rey.bos.telegram.bot.shopping.list.io.repository.UserShoppingListRepository;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;

import java.util.List;

@Service
@AllArgsConstructor
public class UserShoppingListServiceImpl implements UserShoppingListService {

    private final UserShoppingListRepository userShoppingListRepository;
    private final JoinRequestRepository joinRequestRepository;
    private final UserService userService;

    @Override
    public List<UserShoppingListGroupParams> findActiveGroupByListId(long listId) {
        return userShoppingListRepository.findActiveGroupByListId(listId);
    }

    @Override
    public List<UserShoppingList> findActiveGroupByUserId(long userId) {
        return userShoppingListRepository.findActiveGroupByUserId(userId);
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
        User sender = userService.findByIdOrThrow(joinRequest.getUserId());
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
    public UserShoppingList restoreMainList(long userId, UserShoppingList activeList) {
        if (activeList.isOwner()) {
            return activeList;
        }
        activeList.setActive(false);
        userShoppingListRepository.save(activeList);
        List<UserShoppingList> lists = userShoppingListRepository.findByUserIdAndOwner(userId, true);
        checkListCount(lists, userId);
        UserShoppingList ownList = lists.get(0);
        ownList.setActive(true);
        return userShoppingListRepository.save(ownList);
    }

    @Override
    @Transactional
    public void restoreMainList(long userId) {
        UserShoppingList activeList = findActiveUserShoppingList(userId);
        restoreMainList(userId, activeList);
    }

    @Override
    public UserShoppingListGroupParams getUserListParamsById(long userListId) {
        return userShoppingListRepository.getUserListParamsById(userListId);
    }

    @Override
    @Transactional
    public List<Long> disbandGroup(long userId) {
        List<UserShoppingList> group = userShoppingListRepository.findActiveGroupByUserId(userId);
        group = group.stream().filter(item -> item.getUserId() != userId).toList();
        group.forEach(userList -> restoreMainList(userList.getUserId(), userList));
        return group.stream().map(UserShoppingList::getUserId).toList();
    }

    private void checkListCount(List<UserShoppingList> lists, long userId) {
        if (CollectionUtils.isEmpty(lists) || lists.size() != 1) {
            throw new IllegalStateException(
                "The number of active lists for a user with id = " + userId + " is not equal to 1"
            );
        }
    }

}
