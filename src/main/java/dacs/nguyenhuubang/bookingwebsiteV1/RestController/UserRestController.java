
package dacs.nguyenhuubang.bookingwebsiteV1.RestController;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.security.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

	private final UserService userService;

	@GetMapping("/search")
	@ResponseBody
	public List<UserEntity> searchUsers(@RequestParam("q") String q) {
		List<UserEntity> users = userService.search(q);
		return users;
	}

}

/*
	@PostMapping
	public UserEntity createUser(@RequestBody UserEntity user) {
		return userRepository.save(user);
	}



	@PutMapping("{id}")
	public ResponseEntity<UserEntity> updateUser(@RequestBody UserEntity user, @PathVariable int id){
		UserEntity find_user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not exists with id:"+id));
		find_user.setEmail(user.getEmail());
		find_user.setPassword(user.getPassword());
		userRepository.save(find_user);
		return ResponseEntity.ok(find_user);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<HttpStatus> deleteEmployee(@PathVariable int id){
		UserEntity find_user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not exists with id:"+id));
		userRepository.delete(find_user);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

*/



