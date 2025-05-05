package com.klolarion.billusserver.service;

import com.klolarion.billusserver.domain.entity.Menu;
import com.klolarion.billusserver.domain.QMenu;
import com.klolarion.billusserver.domain.entity.Store;
import com.klolarion.billusserver.dto.InfoRequestDto;
import com.klolarion.billusserver.dto.InfoResponseDto;
import com.klolarion.billusserver.exception.r400.BadRequestException;
import com.klolarion.billusserver.domain.repository.MenuRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final JPAQueryFactory query;
    private final MenuRepository menuRepository;


    /**
     * 메뉴 조회
     */
    public InfoResponseDto getMenu(InfoRequestDto requestDto) {
        QMenu qMenu = QMenu.menu;
        Menu savedMenu = query.selectFrom(qMenu)
                .where(qMenu.store.storeName.eq(requestDto.getStoreName())
                        .and(qMenu.date.eq(requestDto.getDate()))
                        .and(qMenu.meal.eq(requestDto.getMeal())))
                .fetchOne();
        if (savedMenu == null) {
            throw new BadRequestException("메뉴를 찾을 수 없습니다.");
        }
        return convertToResponseDto(savedMenu);
    }


    /**
     * 메뉴 등록
     */
    public InfoResponseDto newMenu(InfoRequestDto requestDto, Store store) {
        QMenu qMenu = QMenu.menu;
        Menu fetched = query.selectFrom(qMenu).where(
                qMenu.date.eq(requestDto.getDate())
                        .and(qMenu.store.id.eq(store.getId().toString()))
                        .and(qMenu.meal.eq(requestDto.getMeal()))
        ).fetchOne();
        if (fetched != null) {
            throw new BadRequestException("해당 날짜에 이미 등록된 메뉴가 있습니다.");
        }
        Menu newMenu = Menu.builder()
                .store(store)
                .date(requestDto.getDate())
                .meal(requestDto.getMeal())
                .menu1(requestDto.getMenu1())
                .menu2(requestDto.getMenu2())
                .menu3(requestDto.getMenu3())
                .menu4(requestDto.getMenu4())
                .menu5(requestDto.getMenu5())
                .menu6(requestDto.getMenu6())
                .menu7(requestDto.getMenu7())
                .menu8(requestDto.getMenu8())
                .menu9(requestDto.getMenu9())
                .menu10(requestDto.getMenu10())
                .menu11(requestDto.getMenu11())
                .menu12(requestDto.getMenu12())
                .build();
        Menu savedMenu = menuRepository.save(newMenu);
        return convertToResponseDto(savedMenu);
    }


    /**
     * 메뉴 수정
     */
    public InfoResponseDto updateMenu(InfoRequestDto requestDto) {
        // 기존 메뉴 조회
        Menu menu = menuRepository.findById(Long.valueOf(requestDto.getMenuId()))
                .orElseThrow(() -> new BadRequestException("해당 메뉴를 찾을 수 없습니다."));

        menu.updateMenu(
                requestDto.getMeal(),
                requestDto.getMenu1(),
                requestDto.getMenu2(),
                requestDto.getMenu3(),
                requestDto.getMenu4(),
                requestDto.getMenu5(),
                requestDto.getMenu6(),
                requestDto.getMenu7(),
                requestDto.getMenu8(),
                requestDto.getMenu9(),
                requestDto.getMenu10(),
                requestDto.getMenu11(),
                requestDto.getMenu12()
        );

        Menu savedMenu = menuRepository.save(menu);

        return convertToResponseDto(savedMenu);
    }

    /**
     * 메뉴 삭제
     */
    public void removeMenu(InfoRequestDto requestDto) {
        if (!menuRepository.existsById(Long.valueOf(requestDto.getMenuId()))) {
            throw new BadRequestException("삭제할 메뉴를 찾을 수 없습니다.");
        }

        menuRepository.deleteById(Long.valueOf(requestDto.getMenuId()));
    }

    /**
     * Menu → InfoResponseDto 변환 메서드
     */
    private InfoResponseDto convertToResponseDto(Menu menu) {
        return InfoResponseDto.builder()
                .storeId(menu.getStore().getId().toString())
                .menuId(String.valueOf(menu.getId()))
                .storeName(menu.getStore().getStoreName())
                .meal(menu.getMeal())
                .menu1(menu.getMenu1())
                .menu2(menu.getMenu2())
                .menu3(menu.getMenu3())
                .menu4(menu.getMenu4())
                .menu5(menu.getMenu5())
                .menu6(menu.getMenu6())
                .menu7(menu.getMenu7())
                .menu8(menu.getMenu8())
                .menu9(menu.getMenu9())
                .menu10(menu.getMenu10())
                .menu11(menu.getMenu11())
                .menu12(menu.getMenu12())
                .build();
    }
}
