#ifndef DTEST_DTEST_H
#define DTEST_DTEST_H

#include <iostream>
#include <string>
#include <list>

#define DTEST_PASSED			0
#define DTEST_TIME_LIMIT		1
#define DTEST_MEMORY_LIMIT		2
#define DTEST_OUTPUT_LIMIT		3
#define DTEST_RUNTIME_ERROR		4
#define DTEST_PROCESS_LIMIT		5
#define DTEST_INTERNAL_ERROR	13

#if defined _WIN32 || defined __CYGWIN__
 #define DLL_PUBLIC __declspec(dllexport) // Note: actually gcc seems to also supports this 
 #define DLL_LOCAL
#else
 #if __GNUC__ >= 4
   #define DLL_PUBLIC __attribute__ ((visibility ("default")))
   #define DLL_LOCAL  __attribute__ ((visibility ("hidden")))
 #else
   #define DLL_PUBLIC
   #define DLL_LOCAL
 #endif
#endif

extern bool is_debug;

struct run_limits
{
	unsigned int timeout_msec;
	unsigned int cpu_msec;
	unsigned int mem_bytes;
	unsigned int out_bytes;
	unsigned int proc_num;
};

struct checking_result
{
	unsigned char res_type;
	int ret_value;
};

//__declspec(dllexport) 
__attribute__ ((visibility ("default")))
bool dtest_init();

std::string create_test_dir();

bool delete_test_dir(const std::string& path);

//__declspec(dllexport)
DLL_PUBLIC checking_result check_solution(
	const run_limits& limits,
	const std::string& command,
	const std::string& test_dir_path,
	std::istream& input_test,
	std::ostream& output
	);

//__declspec(dllexport)
DLL_PUBLIC checking_result check_solution(
	const run_limits& limits,
	const std::string& command,
	const std::string& test_dir_path,
	std::istream& input_test,
	std::ostream& output,
	std::ostream& error_stream
	);

//__declspec(dllexport)
DLL_PUBLIC checking_result check_solution_as_user(
	const run_limits& limits,
	const std::string& command,
	const std::string& test_dir_path,
	std::istream& input_test,
	std::ostream& output,
	std::ostream& error_stream,
    const std::string& username,
    const std::string& domain,
    const std::string& password
	);

#endif
